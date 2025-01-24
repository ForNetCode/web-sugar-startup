package com.timzaak

import com.timzaak.controller.AuthCtrl
import redis.clients.jedis.{DefaultJedisClientConfig, HostAndPort, JedisPooled, Protocol}
import very.util.config.WithConfig
import io.circe.generic.auto.*
import io.circe.config.syntax.*
import scalasql.*
import scalasql.PostgresDialect.*
import very.util.keycloak.TapirOIDCAdapter
import very.util.task.WithQuartz

object DI extends WithQuartz with WithConfig {
  // init postgres database
//  import com.timzaak.dao.DB
//  object db extends DB
//  given DB = db

  // ConnectionPool.get().dataSource // This is to get javax.sql.DataSource

  private val dbClient = DbClient.Connection(
    java.sql.DriverManager.getConnection(
      config.getString("db.url"),
      config.getString("db.user"),
      config.getString("db.password")
    ),
    new scalasql.Config {}
  )

  given db: DbApi = dbClient.getAutoCommitClientConnection

  protected lazy val jedisPool = JedisPooled(
    HostAndPort(
      config.getString("redis.host"),
      Option(config.getInt("redis.port")).getOrElse(Protocol.DEFAULT_PORT)
    ),
    DefaultJedisClientConfig
      .builder()
      .password(
        config.as[Option[String]]("redis.password").toOption.flatten.orNull
      )
      .build()
  )

  // object sessionProvider extends RedisSession[UserInfo](jedisPool)

  object oidcAdapter extends TapirOIDCAdapter()
  
  given TapirOIDCAdapter = oidcAdapter
  
  object authCtrl extends AuthCtrl

  // wx

}
