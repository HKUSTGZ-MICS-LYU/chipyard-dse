package chipyard

import org.chipsalliance.cde.config.{Config}

class RocketDSEConfig extends Config(
  new freechips.rocketchip.rocket.WithRocketDSE(1) ++ 
  new chipyard.config.AbstractConfig)

class RocketDSESimConfig extends Config(
  new freechips.rocketchip.subsystem.WithoutTLMonitors ++
  new RocketDSEConfig)

class BoomV3DSEConfig extends Config(
  new boom.v3.common.WithBoomV3DSE(1) ++
  new chipyard.config.WithSystemBusWidth(128) ++
  new chipyard.config.AbstractConfig)

class BoomV3DSESimConfig extends Config(
  new freechips.rocketchip.subsystem.WithoutTLMonitors ++
  new BoomV3DSEConfig)

class BoomV4DSEConfig extends Config(
  new boom.v4.common.WithBoomV4DSE(1) ++
  new chipyard.config.WithSystemBusWidth(128) ++
  new chipyard.config.AbstractConfig)

class BoomV4DSESimConfig extends Config(
  new freechips.rocketchip.subsystem.WithoutTLMonitors ++
  new BoomV4DSEConfig)

class ShuttleDSEConfig extends Config(
  new shuttle.common.WithShuttleDSE ++
  new chipyard.config.AbstractConfig)

class ShuttleDSESimConfig extends Config(
  new freechips.rocketchip.subsystem.WithoutTLMonitors ++
  new ShuttleDSEConfig)