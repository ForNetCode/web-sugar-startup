package com.timzaak

import com.timzaak.dao.DB
import com.typesafe.config.{Config, ConfigFactory}

object DI {
  given config: Config = ConfigFactory.load()

  // init postgres
  //object db extends DB
}
