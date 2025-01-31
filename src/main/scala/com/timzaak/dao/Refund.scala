package com.timzaak.dao

import enumeratum.values.{ ShortCirceEnum, ShortEnum, ShortEnumEntry }
import scalasql.*
import scalasql.PostgresDialect.*
import sttp.tapir.Schema
import sttp.tapir.codec.enumeratum.schemaForShortEnumEntry
import sttp.tapir.docs.apispec.DocsExtensionAttribute.*
import sttp.tapir.json.circe.*
import very.util.persistence.ScalaSQLExtra

import java.sql.{ JDBCType, PreparedStatement, ResultSet }
import java.time.OffsetDateTime

case class Refund[T[_]](
  id: T[Long],
  orderId: T[Long],
  thirdId: T[String],
  reason: T[Option[String]],
  status: T[RefundStatus],
  createdAt: T[OffsetDateTime],
  updatedAt: T[OffsetDateTime],
)

sealed trait RefundStatus(val value: Short) extends ShortEnumEntry

object RefundStatus extends ShortEnum[RefundStatus] with ShortCirceEnum[RefundStatus] {
  case object Pending extends RefundStatus(0)
  case object Success extends RefundStatus(1)
  case object Fail extends RefundStatus(2)
  val values = findValues

  given Schema[RefundStatus] = schemaForShortEnumEntry[RefundStatus]
    .docsExtension("x-enum-varnames", List("Pending", "Success", "Fail"))
    .format("int16")

  given refundStatusTypeMapper: TypeMapper[RefundStatus] =
    new TypeMapper[RefundStatus] {
      def jdbcType: JDBCType = JDBCType.TINYINT
      def get(r: ResultSet, idx: Int): RefundStatus =
        RefundStatus.withValue(r.getShort(idx))
      def put(r: PreparedStatement, idx: Int, v: RefundStatus): Unit =
        r.setShort(idx, v.value)
    }
}

object Refund extends Table[Refund] {
  def newId = ScalaSQLExtra.nextVal("refund_pkey")

  def change(id: Long, status: RefundStatus) = Refund
    .update(v => v.id === id && v.status === RefundStatus.Pending)
    .set(
      _.status := status,
      _.updatedAt := OffsetDateTime.now
    )

}
