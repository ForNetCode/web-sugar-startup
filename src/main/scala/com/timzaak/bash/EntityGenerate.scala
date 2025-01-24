package com.timzaak.bash

import com.typesafe.config.ConfigFactory
import very.util.persistence.pgMigrate

import very.util.persistence.transfer.*

object EntityGenerate {

  @main def dbMigrate = {
    val config = ConfigFactory.load().getConfig("db")
    pgMigrate(config)
  }

  @main def generateEntity: Unit = {
    val config = ConfigFactory.load()
    val url = config.getString("db.url")
    val username = config.getString("db.user")
    val password = config.getString("db.password")
    val model = Model(url, username, password)
    model.allTables().foreach { table =>
      if (table.name != "flyway_schema_history") {
        ScalasqlEntityParser
          .fromTable(Dialect.Postgres, table, "com.timzaak.dao")
          .writeToFile("./src/main/scala")
      }
    }
    // val driver = config.getString("db.driver")
    // Class.forName(driver)

  }

}
