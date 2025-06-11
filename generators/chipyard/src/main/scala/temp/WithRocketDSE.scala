package freechips.rocketchip.rocket

import chisel3.util._

import org.chipsalliance.cde.config._
import org.chipsalliance.diplomacy.lazymodule._

import freechips.rocketchip.prci.{SynchronousCrossing, AsynchronousCrossing, RationalCrossing, ClockCrossingType}
import freechips.rocketchip.subsystem.{TilesLocated, NumTiles, HierarchicalLocation, RocketCrossingParams, SystemBusKey, CacheBlockBytes, RocketTileAttachParams, InSubsystem, InCluster, HierarchicalElementMasterPortParams, HierarchicalElementSlavePortParams, CBUS, CCBUS, ClustersLocated, TileAttachConfig, CloneTileAttachParams}
import freechips.rocketchip.tile.{RocketTileParams, RocketTileBoundaryBufferParams, FPUParams}
import freechips.rocketchip.util.{RationalDirection, Flexible}
import scala.reflect.ClassTag

class WithRocketDSE(
  n: Int,
  location: HierarchicalLocation,
  crossing: RocketCrossingParams,
) extends Config((site, here, up) => {
  case TilesLocated(`location`) => {
    val prev = up(TilesLocated(`location`), site)
    val idOffset = up(NumTiles)
    val big = RocketTileParams(
      core = RocketCoreParams(
        mulDiv = Some(MulDivParams(
          mulUnroll = 8,
          mulEarlyOut = true,
          divEarlyOut = true,
        )),
        useVM = false,
        useZba = true,
        useZbb = true,
        useZbs = true,
        fpu = Some(FPUParams(minFLen = 16))),
      dcache = Some(DCacheParams(
        nSets = 64,
        nWays = 4,
        rowBits = site(SystemBusKey).beatBits,
        nMSHRs = 3,
        blockBytes = site(CacheBlockBytes))),
      icache = Some(ICacheParams(
        nSets = 64,
        nWays = 2,
        nTLBSets = 1,
        nTLBWays = 4,
        rowBits = site(SystemBusKey).beatBits,
        blockBytes = site(CacheBlockBytes))),
      btb = Some(BTBParams(
        nRAS = 9,
        nEntries = 14,
        bhtParams = Some(BHTParams(nEntries=256))
      ))
    )
    List.tabulate(n)(i => RocketTileAttachParams(
      big.copy(tileId = i + idOffset),
      crossing
    )) ++ prev
  }
  case NumTiles => up(NumTiles) + n
}) {
  def this(n: Int, location: HierarchicalLocation = InSubsystem) = this(n, location, RocketCrossingParams(
    master = HierarchicalElementMasterPortParams.locationDefault(location),
    slave = HierarchicalElementSlavePortParams.locationDefault(location),
    mmioBaseAddressPrefixWhere = location match {
      case InSubsystem => CBUS
      case InCluster(clusterId) => CCBUS(clusterId)
    }
  ))
}