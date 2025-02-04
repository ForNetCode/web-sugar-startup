package com.timzaak.controller.response

import com.timzaak.dao.{OrderStatus, PayInfo, Order}
import io.scalaland.chimney.dsl.*

import java.time.OffsetDateTime

case class OrderResp(
    id:Long,
    userId:Int,
    pay:PayInfo,
    refId:String,
    totalAmount: BigDecimal,
    status:OrderStatus,
    createdAt:OffsetDateTime,
    updatedAt: OffsetDateTime)

object OrderResp {
  def fromOrder(order: Order[scalasql.Sc]): OrderResp = {
    order.transformInto[OrderResp]
  }
}
