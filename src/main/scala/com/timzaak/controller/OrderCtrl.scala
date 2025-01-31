package com.timzaak.controller

import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request
import com.github.binarywang.wxpay.service.WxPayService
import com.timzaak.controller.basic.{ AdminBasicController, BadRequest, Role }
import com.timzaak.dao.{ Order, Refund, RefundStatus }
import io.circe.generic.auto.*
import scalasql.DbApi
import scalasql.PostgresDialect.*
import sttp.model.StatusCode
import sttp.tapir.generic.auto.*
import sttp.tapir.*
import sttp.tapir.json.circe.jsonBody

case class RefundReasonReq(reason: Option[String])

trait OrderCtrl(payService: WxPayService) extends AdminBasicController {

  def orderList = roleEndpoint().get
    .in("order" / "list")
    .name("OrderList")
    .in(paging)
    // .out(jsonBody[Seq[OrderResp]])
    .out(jsonBody[Seq[Order[scalasql.Sc]]])
    // .out(jsonBody[Order[scalasql.Sc]])
    .serverLogicPure(userId =>
      page => {
        val result = db.run(
          Order.select
            .sortBy(_.id)
            .desc
            .drop(page.offset)
            .take(page.limit)
        )
        // Right(result.map(OrderResp.fromOrder))
        Right(result)
        // Right(result.head)
      }
    )

  def refund = roleEndpoint(Role.Admin).patch
    .in("order" / "refund" / path[Long]("orderId"))
    .in(jsonBody[RefundReasonReq])
    .name("RefundOrder")
    .out(statusCode(StatusCode.Ok))
    .serverLogicPure { userId => (orderId, reasonReq) =>
      logger.info(s"$userId begin to refund order $orderId")
      db.transaction { _db =>
        val result = _db.run(Order.refundOrder(orderId))
        if (result == 1) {
          try {
            val order = _db.run(Order.getOrder(orderId))
            val refundId = s"r$orderId"
            val req = new WxPayRefundV3Request()
            val amount = new WxPayRefundV3Request.Amount()
            val price = (order.totalAmount * 100).toIntExact
            amount.setTotal(price)
            amount.setRefund(price)
            req.setAmount(amount)
            req.setTransactionId(order.pay.transactionId)
            req.setOutTradeNo(order.id.toString)
            req.setOutRefundNo(refundId)
            given DbApi = _db
            audit(com.timzaak.Module.Order, orderId, "refund", reasonReq.reason) // (_db)
            val response = payService.refundV3(req)
            _db.run(
              Refund.insert.columns(
                _.orderId := orderId,
                _.thirdId := response.getRefundId,
                _.reason := reasonReq.reason,
                _.status := RefundStatus.Pending,
              )
            )
            Right(StatusCode.Ok)
          } catch {
            case e: Throwable =>
              _db.rollback()
              logger.error(s"refund order $orderId fail", e)
              Left(BadRequest("退款失败，请稍后再试"))
          }
        } else {
          _db.rollback()
          Left(BadRequest("订单状态已改变，请确认订单是否已退款"))
        }
      }
    }

  def order = roleEndpoint()
    .in("order" / path[Long]("orderId"))
    .get
    .out(jsonBody[Order[scalasql.Sc]])
    .serverLogicPure(_ => orderId => Right(db.run(Order.getOrder(orderId))))
}
