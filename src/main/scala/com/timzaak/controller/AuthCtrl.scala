package com.timzaak.controller

import com.timzaak.AdminUserInfo
import com.timzaak.controller.basic.AdminBasicController
import sttp.tapir.*
import sttp.model.StatusCode

import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters.*

trait AuthCtrl extends AdminBasicController {

  def test = rolePoint("admin").get
    .in("admin" / "test")
    .out(stringBody)
    .serverLogicPure(id => _ => Right(id))

  def session = oidcAdapter
    .tokenExtract[Future]
    .get
    .in("auth" / "session")
    .out(stringBody)
    .serverLogicPure { userProfile => _ =>
      val userId = userProfile.getId
      val clientName = userProfile.getClientName
      val sessionId = userProfile.getAttribute("sid").asInstanceOf[String]
      val roles = userProfile.getRoles.asScala.toSet
      // TODO: assert azp == clientId
      val expiredTime =
        (userProfile
          .getAttribute("exp")
          .asInstanceOf[java.util.Date]
          .getTime - System.currentTimeMillis())/1000 + 20
      sessionProvider.setSession(
        sessionId,
        AdminUserInfo(userId, roles),
        Some(Duration(expiredTime, TimeUnit.SECONDS))
      )
      Right(sessionId)
    }

}
