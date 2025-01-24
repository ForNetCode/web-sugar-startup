package com.timzaak.wechat.controller.basic

import com.timzaak.{ SessionProvider, UserInfo }
import sttp.tapir.*
import very.util.web.BasicController

import scala.concurrent.Future

trait WXAppBasicController(val sessionProvider: SessionProvider)
  extends BasicController {
  protected val wx = com.timzaak.wechat.controller.basic.route
    .in("wx")
    .tag("wechat")

  protected val wxAuth = wx
    .securityIn(
      auth
        .apiKey(
          header[String]("Authorization").validate(Validator.nonEmptyString)
        )
        .securitySchemeName("wxKey")
        .description("SessionId")
    )
    .serverSecurityLogicPure[UserInfo, Future] { token =>
      sessionProvider.session(token) match {
        case Some(user) => Right(user)
        case None => Left(com.timzaak.wechat.controller.basic.Unauthorized())
      }
    }
}
