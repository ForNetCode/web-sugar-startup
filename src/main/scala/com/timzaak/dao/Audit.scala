package com.timzaak.dao

import com.timzaak.Module
import scalasql.*
import scalasql.PostgresDialect.*
import sttp.tapir.Schema
import sttp.tapir.Schema.annotations.encodedName

import java.time.OffsetDateTime

@encodedName("Audit")
case class Audit[T[_]](
  id: T[Long],
  module: T[Module],
  refId: T[Long],
  action: T[String],
  description: T[Option[String]],
  createdAt: T[OffsetDateTime],
)

object Audit extends Table[Audit] {
  given Schema[Audit[Sc]] = Schema.derived
  
  def create(module: Module, refId: Int | Long, action: String, description: Option[String] = None)(using db: DbApi) = {
    val _refId = refId match {
      case v: Int  => v.toLong
      case v: Long => v
    }
    db.run(
      Audit.insert
        .columns(
          _.refId := _refId,
          _.module := module,
          _.action := action,
          _.description := description
        )
        .returning(_.id)
        .single
    )
  }
}
