package com.timzaak
import enumeratum.values.*
import sttp.tapir.codec.enumeratum.schemaForStringEnumEntry
import sttp.tapir.docs.apispec.DocsExtensionAttribute.*
import sttp.tapir.json.circe.*
import redis.clients.jedis.JedisPooled
import scalasql.TypeMapper
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.{ Codec, DecodeResult, Schema }
import very.util.web.RedisSession

import java.sql.{ JDBCType, PreparedStatement, ResultSet }

type Redis = JedisPooled

type UserInfo = Int
type SessionProvider = RedisSession[UserInfo]

//type AdminUserInfo = (String, Set[String]) // (UserId, Roles)
case class AdminUserInfo(userId: String, roles: Set[String])

type AdminSessionProvider = RedisSession[AdminUserInfo]

sealed abstract class Module(val value: String) extends StringEnumEntry

object Module extends StringEnum[Module] with StringCirceEnum[Module] {
  case object Order extends Module("order")

  val values = findValues

  // for tapir jsonBody
  given Schema[Module] = {
    schemaForStringEnumEntry[Module].docsExtension("x-enum-varnames", List("Order"))
  }
  // for scalasql
  given moduleTypeMapper: TypeMapper[Module] = new TypeMapper[Module] {
    def jdbcType: JDBCType = JDBCType.VARCHAR
    def get(r: ResultSet, idx: Int): Module = Module.withValue(r.getString(idx))
    def put(r: PreparedStatement, idx: Int, v: Module): Unit = r.setString(idx, v.value)
  }

  // for tapir query
  given PlainCodec[Module] = Codec.string.mapEither(v => Module.withValueEither(v).left.map(_.notFoundValue))(_.value)
}
