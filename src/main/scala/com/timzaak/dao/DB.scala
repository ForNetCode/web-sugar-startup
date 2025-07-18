package com.timzaak.dao

import very.util.config.WithConfig
import very.util.persistence.DBHelper
import scalasql.DbClient
import com.zaxxer.hikari.HikariDataSource

/**
 * Init Database Connection
 */
trait DB extends WithConfig {
  import scalasql.PostgresDialect.*
  private val dataSource = HikariDataSource()
  dataSource.setJdbcUrl(config.getString("db.url"))
  dataSource.setUsername(config.getString("db.user"))
  dataSource.setPassword(config.getString("db.password"))

  given dbClient: DbClient = DbClient.DataSource(dataSource)

  given DBHelper = DBHelper(dbClient)
}
