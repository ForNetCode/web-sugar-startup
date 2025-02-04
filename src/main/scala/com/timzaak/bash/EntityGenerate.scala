package com.timzaak.bash

import com.typesafe.config.ConfigFactory
import very.util.persistence.pgMigrate
import very.util.persistence.transfer.*

case class ScalasqlEntityParserDefined(parse: ScalasqlEntityParser) extends WriteToFile {
  export parse.{ schema as _, * }
  override def schema: String =
    s"""${parse.schema}{
       |  given Schema[${parse.name}[Sc]] = Schema.derived
       |}""".stripMargin
}

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
        ScalasqlEntityParserDefined(
          ScalasqlEntityParser
            .fromTable(
              Dialect.Postgres,
              table,
              "com.timzaak.dao",
              imports = List("sttp.tapir.Schema", "sttp.tapir.Schema.annotations.encodedName"),
              annotations = List(s"@encodedName(${table.name})")
            )
        )
          .writeToFile("./src/main/scala")
      }
    }
    // val driver = config.getString("db.driver")
    // Class.forName(driver)

  }

}
