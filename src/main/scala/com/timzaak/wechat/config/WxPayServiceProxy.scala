package com.timzaak.wechat.config

import com.github.binarywang.wxpay.service.WxPayService
import com.typesafe.config.Config
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl
import io.circe.generic.auto.*
import io.circe.config.syntax.*

case class WxPayConfig(
  appId: String,
  mchId: String,
  apiV3key: String,
  certSerialNo: String,
  privateKeyPath: String,
  privateCertPath: String,
  notifyUrl: String,
  sandbox: Option[Boolean] = Some(false)
)
object WxPayConfig {
  def apply(config: Config): WxPayConfig = config.as[WxPayConfig].fold(e => throw e, identity)
}

class WxPayServiceProxy(conf: WxPayConfig) extends WxPayService {

  private val service = {
    val payConfig = com.github.binarywang.wxpay.config.WxPayConfig()
    // payConfig.setServiceId()
    payConfig.setAppId(conf.appId)
    payConfig.setMchId(conf.mchId)
    payConfig.setApiV3Key(conf.apiV3key)
    payConfig.setCertSerialNo(conf.certSerialNo)
    payConfig.setPrivateKeyPath(conf.privateKeyPath)
    payConfig.setPrivateCertPath(conf.privateCertPath)

    payConfig.setUseSandboxEnv(conf.sandbox.exists(identity))

    val wxPayService = WxPayServiceImpl()
    wxPayService.setConfig(payConfig)
    wxPayService
  }

  export service.*
}
