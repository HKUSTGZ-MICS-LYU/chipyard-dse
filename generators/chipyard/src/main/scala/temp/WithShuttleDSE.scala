package shuttle.common

import chisel3._
import chisel3.util.{log2Up}

import org.chipsalliance.cde.config.{Parameters, Config, Field}
import freechips.rocketchip.subsystem._
import freechips.rocketchip.devices.tilelink.{BootROMParams}
import freechips.rocketchip.prci.{SynchronousCrossing, AsynchronousCrossing, RationalCrossing}
import freechips.rocketchip.rocket._
import freechips.rocketchip.tile._
import shuttle.dmem.{ShuttleSGTCMParams, ShuttleDCacheParams}

class WithShuttleDSE(
  n: Int,
  location: HierarchicalLocation,
  crossing: ShuttleCrossingParams,
) extends Config((site, here, up) => {
  case TilesLocated(`location`) => {
    val prev = up(TilesLocated(location), site)
    val idOffset = up(NumTiles)
      (0 until n).map { i =>
        ShuttleTileAttachParams(
          tileParams = ShuttleTileParams(
            core = ShuttleCoreParams(
                nL2TLBEntries = 512,
                nL2TLBWays = 1,
                fetchWidth = 8,
                retireWidth = 4,
                ),
            icache = Some(ICacheParams(
                rowBits = -1, nSets=64, nWays=8, fetchBytes=2*8)),
            btb = Some(BTBParams(
                nRAS = 3,
                nEntries=32, 
                bhtParams = Some(BHTParams(counterLength=2)))),
            dcacheParams = ShuttleDCacheParams(nSets = 64, nWays = 4, nMSHRs = 4),
            tileId = i + idOffset
          ),
          crossingParams = crossing
        )
      } ++ prev
    }
  case NumTiles => up(NumTiles) + n
}) {
  def this(n: Int = 1, location: HierarchicalLocation = InSubsystem) = this(n, location, ShuttleCrossingParams(
    master = HierarchicalElementMasterPortParams.locationDefault(location),
    slave = location match {
      case InSubsystem => HierarchicalElementSlavePortParams(where=SBUS)
      case InCluster(clusterId) => HierarchicalElementSlavePortParams(where=CSBUS(clusterId), blockerCtrlWhere=CCBUS(clusterId))
    },
    mmioBaseAddressPrefixWhere = location match {
      case InSubsystem => CBUS
      case InCluster(clusterId) => CCBUS(clusterId)
    }
  ))
}