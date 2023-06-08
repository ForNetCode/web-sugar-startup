package com.timzaak

import com.typesafe.config.{Config, ConfigFactory}

object DI {
  given config: Config = ConfigFactory.load()
}
