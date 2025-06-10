package chipyard

import org.chipsalliance.cde.config.{Config}

class FastRocketSimConfig extends Config(
  new freechips.rocketchip.subsystem.WithoutTLMonitors ++
  new RocketConfig)