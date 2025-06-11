package chipyard

import org.chipsalliance.cde.config.{Config}

class RocketDSEConfig extends Config(
  new freechips.rocketchip.rocket.WithRocketDSE(1) ++ 
  new chipyard.config.AbstractConfig)

class RocketDSESimConfig extends Config(
  new freechips.rocketchip.subsystem.WithoutTLMonitors ++
  new RocketDSEConfig)