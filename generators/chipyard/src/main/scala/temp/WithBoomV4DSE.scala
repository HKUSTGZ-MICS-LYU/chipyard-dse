package boom.v4.common

import chisel3._
import chisel3.util.{log2Up}

import org.chipsalliance.cde.config.{Parameters, Config, Field}
import freechips.rocketchip.subsystem._
import freechips.rocketchip.devices.tilelink.{BootROMParams}
import freechips.rocketchip.prci.{SynchronousCrossing, AsynchronousCrossing, RationalCrossing}
import freechips.rocketchip.rocket._
import freechips.rocketchip.tile._

import boom.v4.ifu._
import boom.v4.exu._
import boom.v4.lsu._


class WithBoomV4DSE(n: Int = 1) extends Config(
  new WithTAGELBPD ++ // Default to TAGE-L BPD
  new Config((site, here, up) => {
    case TilesLocated(InSubsystem) => {
      val prev = up(TilesLocated(InSubsystem), site)
      val idOffset = up(NumTiles)
      (0 until n).map { i =>
        BoomTileAttachParams(
          tileParams = BoomTileParams(
            core = BoomCoreParams(
              fetchWidth = 8,
              decodeWidth = 4,
              numRobEntries = 128,
              issueParams = Seq(
                IssueParams(issueWidth=3, numEntries=32, iqType=IQ_MEM, dispatchWidth=4),
                IssueParams(issueWidth=1, numEntries=20, iqType=IQ_UNQ, dispatchWidth=4),
                IssueParams(issueWidth=4, numEntries=40, iqType=IQ_ALU, dispatchWidth=4),
                IssueParams(issueWidth=2, numEntries=32, iqType=IQ_FP , dispatchWidth=4)),
              lsuWidth = 2,
              numIntPhysRegisters = 144,
              numFpPhysRegisters = 128,
              numIrfReadPorts = 4,
              numIrfBanks = 4,
              numFrfReadPorts = 6,
              numFrfBanks = 1,
              numLdqEntries = 32,
              numStqEntries = 32,
              maxBrCount = 20,
              numFetchBufferEntries = 32,
              enablePrefetching = true,
              enableSuperscalarSnapshots = true,
              enableFastLoadUse = true,
              numDCacheBanks = 4,
              ftq = FtqParameters(nEntries=40),
              fpu = Some(freechips.rocketchip.tile.FPUParams(sfmaLatency=4, dfmaLatency=4, divSqrt=true))
            ),
            dcache = Some(
              DCacheParams(rowBits = 128, nSets=64, nWays=8, nMSHRs=8, nTLBWays=32)
            ),
            icache = Some(
              ICacheParams(rowBits = 128, nSets=64, nWays=8, fetchBytes=4*4)
            ),
            tileId = i + idOffset
          ),
          crossingParams = RocketCrossingParams()
        )
      } ++ prev
    }
    case NumTiles => up(NumTiles) + n
  })
)