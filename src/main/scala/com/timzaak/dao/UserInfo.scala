package com.timzaak.dao

import scalasql.*
import scalasql.PostgresDialect.*
import sttp.tapir.Schema.annotations.encodedName

import java.time.OffsetDateTime

@encodedName("UserInfo")
case class UserInfo[T[_]](
  id: T[Int],
  nickname: T[Option[String]],
  createdAt: T[OffsetDateTime],
  updatedAt: T[OffsetDateTime],
)

object UserInfo extends Table[UserInfo]
