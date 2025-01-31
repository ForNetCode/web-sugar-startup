package com.timzaak.wechat.controller

import cn.binarywang.wx.miniapp.api.WxMaService
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result.JsapiResult
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum
import com.github.binarywang.wxpay.service.WxPayService
import com.timzaak.dao.PayChannel.WeChat
import com.timzaak.dao.{ Account, Order, PayInfo, RefundStatus, Refund }
import com.timzaak.wechat.controller.basic.{ BadRequest, WXAppBasicController }
import scalasql.*
import sttp.model.HeaderNames
import sttp.tapir.*
import io.scalaland.chimney.dsl.*
import io.circe.generic.auto.{ *, given }
import sttp.tapir.generic.auto.*
import very.util.persistence.DBHelper

import scala.concurrent.Future
import scala.util.{ Failure, Success, Try }

case class JsapiResultWrapper(
  appId: String,
  timeStamp: String,
  nonceStr: String,
  packageValue: String,
  signType: String,
  paySign: String,
)

trait WeChatAppCtrl(maService: WxMaService, payService: WxPayService)(using
  db: DBHelper
) extends WXAppBasicController {

  private val realIp =
    extractFromRequest(request =>
      request
        .header(HeaderNames.XRealIp)
        .orElse(request.header(HeaderNames.XForwardedFor))
        .orElse(request.header(HeaderNames.RemoteAddress))
        .getOrElse(
          request.connectionInfo.remote.map(_.getAddress.getHostAddress).get
        )
    )

  wxAuth.post
    .in("order" / "create")
    .description("创建订单")
    .serverLogicSuccess { ip =>
      ???
    }

  wxAuth.put
    .in("order" / "pay" / path[Long]("orderId").description("order Id"))
    .name("微信小程序支付订单")
    .out(jsonBody[JsapiResultWrapper].description("微信支付调用返回"))
    .serverLogicPure(_payOrder.curried)

  val code2Session = wx.get
    .in(
      "session" / path[String]("code")
        .validate(Validator.nonEmptyString)
        .description("wx code")
    )
    .name("小程序code置换session")
    .out(stringBody.description("SessionId"))
    .serverLogicSuccess { code =>
      Future.fromTry(_code2Session(code))
    }

  val payNotify = wx.post
    .in("notify" / "order")
    .name("wx pay order notify")
    .in(stringBody)
    .out(stringBody)
    .serverLogicSuccessPure(_orderCallback)

  val refundNotify = wx.post
    .in("notify" / "refund")
    .name("wx refund order notify")
    .in(stringBody)
    .out(stringBody)
    .serverLogicSuccessPure(???)

  private def _code2Session(code: String) = {
    Try {
      val result = maService.jsCode2SessionInfo(code)
      val id = db
        .run(Account.save(result.getOpenid, Option(result.getUnionid)))
      val token = result.getOpenid
      sessionProvider.setSession(token, id)
      token
    }
  }

  // https://github.com/binarywang/weixin-java-pay-demo/blob/master/src/main/java/com/github/binarywang/demo/wx/pay/controller/WxPayController.java
  private def _payOrder(
    user: com.timzaak.UserInfo,
    orderId: Long
  ) = {
    db.run(Order.needPaidOrder(orderId, user)).headOption match {
      case Some(sumAmount) =>
        val openId = db.run(Account.getOpenId(user))
        val req = WxPayUnifiedOrderV3Request()
        val amount = WxPayUnifiedOrderV3Request.Amount()
        val payer = WxPayUnifiedOrderV3Request.Payer()
        payer.setOpenid(openId)
        amount.setTotal((sumAmount * 100).toIntExact)
        val c = payService.getConfig
        req.setAppid(c.getAppId)
        req.setMchid(c.getMchId)
        req.setPayer(payer)
        req.setAmount(amount)
        req.setNotifyUrl(c.getNotifyUrl)
        req.setOutTradeNo(orderId.toString)
        req.setDescription(???)
        Try {
          val jsapiResult = payService
            .createOrderV3[JsapiResult](TradeTypeEnum.JSAPI, req) // 小程序
          transparent inline given cfg: TransformerConfiguration[?] =
            TransformerConfiguration.default.enableBeanGetters
          jsapiResult.transformInto[JsapiResultWrapper]

        } match {
          case Success(value) => Right(value)
          case Failure(e) =>
            logger.error("create wechat order failure", e)
            Left(BadRequest("支付订单失败，请稍后再试"))
        }
      case _ =>
        Left(BadRequest("订单不存在或已支付"))
    }
  }

  private def _orderCallback(body: String): String = {
    logger.info(s"order callback: $body")
    Try {
      payService.parseOrderNotifyResult(body)
    } match {
      case Success(result) =>
        val orderId = result.getOutTradeNo.toLong
        val fee = BigDecimal(result.getTotalFee) / 100
        val transactionId = result.getTransactionId
        val payInfo = PayInfo(WeChat, transactionId)
        val r = db.run(Order.paySuccess(orderId, payInfo))
        if (r > 0) {
          logger.info(s"${orderId} paid success, transactionId:$transactionId")
        } else {
          logger.info(s"${orderId} paid repeated")
        }
        WxPayNotifyResponse.success("OK")
      case Failure(exception) =>
        logger.error(s"parse wx callback failure: $body", exception)
        WxPayNotifyResponse.fail("Fail")
    }
  }

  private def _refundCallback(body: String): String = {
    Try {
      val result = payService.parseRefundNotifyResult(body)
      logger.info(s"refund callback: $body")
      val reqInfo = result.getReqInfo
      val refundId = reqInfo.getOutRefundNo.drop(1).toLong
      val orderId = reqInfo.getOutTradeNo.toLong
      val refundStatus = reqInfo.getRefundStatus match {
        case "SUCCESS"                => RefundStatus.Success
        case "CHANGE" | "REFUNDCLOSE" => RefundStatus.Fail
      }
      db.transaction { _db =>
        val result = _db.run(Order.close(orderId))
        if (result > 0) {
          _db.run(Refund.change(refundId, refundStatus))
        }
      }
    } match {
      case Success(_) => WxPayNotifyResponse.success("OK")
      case Failure(e) =>
        logger.error(s"parse wx callback failure: $body", e)
        WxPayNotifyResponse.fail("Fail")
    }
  }
}
