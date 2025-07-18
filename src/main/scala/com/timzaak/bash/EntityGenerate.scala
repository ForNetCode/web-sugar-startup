package com.timzaak.bash

import com.typesafe.config.ConfigFactory
import very.util.persistence.pgMigrate

object EntityGenerate {

  @main def dbMigrate = {
    pgMigrate(ConfigFactory.load().getConfig("db.default"))
  }

  // Note: Entity generation is now handled manually with ScalaSQL
  // You can define your table schemas directly using ScalaSQL's Table definitions
  @main def generateEntity: Unit = {
    println("Entity generation with ScalaSQL should be done manually.")
    println("Please define your table schemas using ScalaSQL Table definitions.")
    println("See ScalaSQL documentation for examples: https://github.com/com-lihaoyi/scalasql")
  }

}
