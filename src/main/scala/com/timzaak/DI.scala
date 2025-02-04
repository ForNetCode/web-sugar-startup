package com.timzaak

import com.timzaak.controller.{ AuditCtrl, AuthCtrl, OrderCtrl }
import com.timzaak.service.OrderService
import com.timzaak.service.task.OrderExpireTask
import com.timzaak.wechat.config.{ WxPayConfig, WxPayServiceProxy }
import com.zaxxer.hikari.HikariDataSource
import redis.clients.jedis.{ DefaultJedisClientConfig, HostAndPort, JedisPooled, Protocol }
import very.util.config.WithConfig
import io.circe.generic.auto.*
import io.circe.config.syntax.*
import scalasql.*
import scalasql.PostgresDialect.*
import very.util.keycloak.TapirOIDCAdapter
import very.util.persistence.DBHelper
import very.util.task.{ QuartzManager, WithQuartz }
import very.util.web.RedisSession

object DI extends WithQuartz with WithConfig {

  given QuartzManager = quartzManager

  private val dataSource = HikariDataSource()
  dataSource.setJdbcUrl(config.getString("db.url"))
  dataSource.setUsername(config.getString("db.user"))
  dataSource.setPassword(config.getString("db.password"))
  given dbClient: DbClient = DbClient.DataSource(dataSource)

  /*
  given dbClient: DbClient = DbClient.Connection(
    java.sql.DriverManager.getConnection(
      config.getString("db.url"),
      config.getString("db.user"),
      config.getString("db.password")
    ),
    new scalasql.Config {}
  )
   */

  given DBHelper = DBHelper(dbClient)
  // given db: DbApi = dbClient.getAutoCommitClientConnection

  protected lazy val jedisPool = JedisPooled(
    HostAndPort(
      config.getString("redis.host"),
      config.as[Int]("redis.port").getOrElse(Protocol.DEFAULT_PORT)
    ),
    DefaultJedisClientConfig
      .builder()
      .password(
        config.as[Option[String]]("redis.password").toOption.flatten.orNull
      )
      .build()
  )

  given _adminSessionProvider: AdminSessionProvider =
    new RedisSession[AdminUserInfo](jedisPool, prefix = "a:") {}

  object oidcAdapter extends TapirOIDCAdapter()

  given TapirOIDCAdapter = oidcAdapter

  object wxPayService extends WxPayServiceProxy(WxPayConfig(config.getConfig("wx.pay")))

  object orderService extends OrderService

  object authCtrl extends AuthCtrl

  object orderCtrl extends OrderCtrl(wxPayService)

  object auditCtrl extends AuditCtrl

  // wx

  // scheduler
  OrderExpireTask.schedule(orderService)

}
