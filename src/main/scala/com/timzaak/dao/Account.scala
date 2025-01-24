package com.timzaak.dao

import scalasql.query.Query
import scalasql.*
import scalasql.PostgresDialect.*

import java.time.OffsetDateTime

case class Account[T[_]](
  id: T[Int],
  openId: T[String],
  unionId: T[Option[String]],
  createdAt: T[OffsetDateTime],
  updatedAt: T[OffsetDateTime]
)
object Account extends Table[Account]() {
  def save(openId: String, unionId: Option[String]): Query.Single[Int] =
    Account.insert
      .columns(
        _.openId := openId,
        _.unionId := unionId
      )
      .onConflictIgnore(_.openId)
      .returning(_.id)
      .single

  def getOpenId(userId:Int) = Account.select.filter(_.id === userId).map(_.openId).single
}
