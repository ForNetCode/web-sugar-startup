package com.timzaak.dao

import enumeratum.values.*
import org.postgresql.util.PGobject
import scalasql.*
import scalasql.PostgresDialect.*
import sttp.tapir.Schema
import sttp.tapir.Schema.annotations.encodedName
import very.util.web.JsonConfig
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.docs.apispec.DocsExtensionAttribute.*

import java.sql.{ JDBCType, PreparedStatement, ResultSet }
import java.time.OffsetDateTime

@encodedName("Order")
case class Order[T[_]](
  id: T[Long],
  userId: T[Int],
//info: T[Any],
  pay: T[PayInfo],
  refId: T[String],
  addressSnapId: T[Int],
  totalAmount: T[BigDecimal],
  status: T[OrderStatus],
  note: T[String],
  createdAt: T[OffsetDateTime],
  updatedAt: T[OffsetDateTime],
)

sealed abstract class OrderStatus(val value: Short) extends ShortEnumEntry

object OrderStatus extends ShortEnum[OrderStatus] with ShortCirceEnum[OrderStatus] {
  case object UnPaid extends OrderStatus(0)
  case object Paid extends OrderStatus(1)
  case object Finish extends OrderStatus(2)
  case object Refunding extends OrderStatus(3)
  case object Cancel extends OrderStatus(4)
  val values = findValues

  import sttp.tapir.codec.enumeratum.schemaForShortEnumEntry
  given Schema[OrderStatus] = {
    schemaForShortEnumEntry[OrderStatus]
      .docsExtension("x-enum-varnames", List("UnPaid", "Paid", "Finish", "Refunding", "Cancel"))
      .format("int16")
  }

  given orderStatusTypeMapper: TypeMapper[OrderStatus] =
    new TypeMapper[OrderStatus] {
      def jdbcType: JDBCType = JDBCType.TINYINT
      def get(r: ResultSet, idx: Int): OrderStatus =
        OrderStatus.withValue(r.getShort(idx))
      def put(r: PreparedStatement, idx: Int, v: OrderStatus): Unit =
        r.setShort(idx, v.value)
    }
}

// @customise(s =>s.docsExtension("x-enum-varnames", List("WeChat", "Alipay")))
sealed abstract class PayChannel(val value: Int) extends IntEnumEntry
object PayChannel extends IntEnum[PayChannel] with IntCirceEnum[PayChannel] {
  case object WeChat extends PayChannel(0)
  case object Alipay extends PayChannel(1)
  val values = findValues

  import sttp.tapir.codec.enumeratum.schemaForIntEnumEntry
  given Schema[PayChannel] = schemaForIntEnumEntry[PayChannel]
    .docsExtension("x-enum-varnames", List("WeChat", "Alipay"))
}

case class PayInfo(channel: PayChannel, transactionId: String)

object PayInfo {
  import io.circe.syntax.*
  import io.circe.generic.auto.*
  import io.circe.parser.*

  given payInfoTypeMapper: TypeMapper[PayInfo] = new TypeMapper[PayInfo] {
    override def jdbcType: JDBCType = JDBCType.OTHER

    override def get(r: ResultSet, idx: Int): PayInfo =
      parse(r.getString(idx)).toOption.get.as[PayInfo].toOption.get

    override def put(r: PreparedStatement, idx: Int, v: PayInfo): Unit = {
      val jsonObject = PGobject()
      jsonObject.setType("jsonb")
      jsonObject.setValue(JsonConfig.jsonPrinter.print(v.asJson))
      r.setObject(idx, jsonObject)
    }
  }
}

object Order extends Table[Order] {

  override def tableName: String = "orders"

  given Schema[Order[Sc]] = Schema.derived
  def paySuccess(orderId: Long, payInfo: PayInfo) = Order
    .update(v => v.id === orderId && v.status === OrderStatus.UnPaid)
    .set(
      _.status := OrderStatus.Paid,
      _.pay := payInfo,
      _.updatedAt := OffsetDateTime.now()
    )

  def getOrder(orderId: Long) = Order.select.filter(_.id === orderId).single
  def needPaidOrder(orderId: Long, userId: Int) = Order.select
    .filter(v => v.id === orderId && v.status === OrderStatus.UnPaid)
    .map(_.totalAmount)
    .take(1)

  def refundOrder(orderId: Long) = Order
    .update(v => v.id === orderId && v.status === OrderStatus.Paid)
    .set(
      _.status := OrderStatus.Refunding,
      _.updatedAt := OffsetDateTime.now()
    )

  def close(orderId: Long) = Order
    .update(v => v.id === orderId && (v.status !== OrderStatus.Cancel))
    .set(
      _.status := OrderStatus.Cancel,
      _.updatedAt := OffsetDateTime.now()
    )

  def setOrderExpired(begin: OffsetDateTime, end: OffsetDateTime) = {
    Order
      .update(v => v.status === OrderStatus.UnPaid && v.updatedAt < end && v.updatedAt >= begin)
      .set(
        _.status := OrderStatus.Cancel,
        _.updatedAt := OffsetDateTime.now()
      )
      .returning(_.id)
  }

}
