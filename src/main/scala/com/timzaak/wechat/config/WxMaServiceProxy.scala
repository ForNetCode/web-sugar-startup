package com.timzaak.wechat.config

import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl
import com.typesafe.config.Config
import io.circe.generic.auto.*
import io.circe.config.syntax.*

case class WxMaConfig(
  appId: String,
  secret: String,
  aesKey: String,
  token: String,
  tokenRefresh: Boolean = false
)
object WxMaConfig {
  def apply(config: Config) = config.as[WxMaConfig].toOption.get
}

class WxMaServiceProxy(_conf: WxMaConfig) extends WxMaService {
  private val service = {
    // val _conf =
    val r = WxMaDefaultConfigImpl()
    r.setAppid(_conf.appId)
    r.setSecret(_conf.secret)
    r.setAesKey(_conf.aesKey)
    r.setToken(_conf.token)
    val service = WxMaServiceImpl()
    service.setWxMaConfig(r)
    service
  }
  export service.*

  service.getAccessToken

}
