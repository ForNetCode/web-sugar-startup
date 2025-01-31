package com.timzaak.controller.basic

import com.timzaak.AdminSessionProvider
import com.timzaak.dao.Audit
import sttp.model.StatusCode
import very.util.keycloak.{ ErrorResp, PermissionInvalid, TapirOIDCAdapter, TokenInvalid }
import sttp.tapir.server.model.EndpointExtensions.*
import sttp.tapir.*
import very.util.persistence.DBHelper
import very.util.web.BasicController

import scala.concurrent.Future

sealed trait BusinessError extends ErrorResp

case class BadRequest(msg: String) extends BusinessError

enum Role {
  case Admin
}

trait AdminBasicController(using
  protected val oidcAdapter: TapirOIDCAdapter,
  protected val sessionProvider: AdminSessionProvider,
  protected val db: DBHelper,
) extends BasicController {

  protected def rolePoint(roles: String*) = oidcAdapter
    .hasRole[Future](roles*)
    .errorOutVariantsPrepend(
      oneOfVariant(
        statusCode(StatusCode.BadRequest).and(stringBody.mapTo[BadRequest]),
      )
    )
    .maxRequestBodyLength(1024 * 2 * 1024 /*2M*/ )

  protected def roleEndpoint(roles: Role*) =
    endpoint
      .errorOut(
        oneOf[ErrorResp](
          oneOfVariant(
            statusCode(StatusCode.Forbidden)
              .and(stringBody.mapTo[PermissionInvalid])
          ),
          oneOfVariant(
            statusCode(StatusCode.Unauthorized)
              .and(stringBody.mapTo[TokenInvalid])
          ),
          oneOfVariant(
            statusCode(StatusCode.BadRequest).and(stringBody.mapTo[BadRequest]),
          )
        )
      )
      .securityIn(
        auth
          .bearer[String]()
          .securitySchemeName("adminRole")
          .description("sessionId")
      )
      .serverSecurityLogicPure[String, Future] { token =>
        sessionProvider.session(token) match {
          case Some(user) if roles.exists(role => user.roles.contains(role.toString.toLowerCase)) || roles.isEmpty =>
            Right(user.userId)
          case Some(_) => Left(PermissionInvalid())
          case None    => Left(TokenInvalid())
        }
      }

  protected val audit = Audit.create
}
