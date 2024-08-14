package com.timzaak.bash

import com.typesafe.config.ConfigFactory
import very.util.persistence.pgMigrate
import very.util.persistence.scalikejdbc.mapper.{CodeGenerator, DateTimeClass, GeneratorConfig, Model}

object EntityGenerate {

  @main def dbMigrate = {
    pgMigrate(ConfigFactory.load().getConfig("db.default"))
  }

  @main def generateEntity: Unit = {
    val config = ConfigFactory.load()
    val url = config.getString("db.default.url")
    val username = config.getString("db.default.user")
    val password = config.getString("db.default.password")
    val driver = config.getString("db.default.driver")
    Class.forName(driver)

    val model = Model(url, username, password)

    val generatorConfig = GeneratorConfig(
      packageName = "com.timzaak.entity",
      dateTimeClass = DateTimeClass.JodaDateTime,
      daoExtendImport = Some("very.util.persistence.scalikejdbc.Dao")
    )
    // where table to generate
    val tables:Map[String, String] = Map(
      //      "consignor"-> "Consignor",
      //      "fee_config" -> "FeeConfig",
    )
    model.allTables("public").foreach { table =>
      if (tables.contains(table.name)) {
        new CodeGenerator(table, None)(generatorConfig.copy(tableNameToClassName = (a: String) => tables(a))).writeModel()
      }
    }
  }

}