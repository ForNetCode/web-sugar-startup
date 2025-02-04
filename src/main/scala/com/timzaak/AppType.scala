package com.timzaak
import enumeratum.values.*
import sttp.tapir.docs.apispec.DocsExtensionAttribute.*
import redis.clients.jedis.JedisPooled
import scalasql.TypeMapper
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.Schema
import very.util.persistence.ScalaSQLExtra.stringEnumTypeMapper
import very.util.web.RedisSession
import very.util.web.TapirExtra.{ stringEnumCodec, stringEnumSchema }

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
  given Schema[Module] = stringEnumSchema[Module]
  // for scalasql
  given moduleTypeMapper: TypeMapper[Module] = stringEnumTypeMapper[Module]

  // for tapir query
  given PlainCodec[Module] = stringEnumCodec[Module]
}
