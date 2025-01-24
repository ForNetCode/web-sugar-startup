package com.timzaak.controller.basic

import com.timzaak.AdminSessionProvider
import sttp.model.StatusCode
import very.util.keycloak.{
  ErrorResp,
  PermissionInvalid,
  TapirOIDCAdapter,
  TokenInvalid
}
import sttp.tapir.server.model.EndpointExtensions.*
import sttp.tapir.*
import very.util.web.LogSupport

import scala.concurrent.Future

sealed trait BusinessError extends ErrorResp

case class BadRequest(msg: String) extends BusinessError

trait AdminBasicController(using
  protected val oidcAdapter: TapirOIDCAdapter,
  protected val sessionProvider: AdminSessionProvider
) extends LogSupport {

  protected def rolePoint(roles: String*) = oidcAdapter
    .hasRole[Future](roles*)
    .errorOutVariantsPrepend(
      oneOfVariant(
        statusCode(StatusCode.BadRequest).and(stringBody.mapTo[BadRequest]),
      )
    )
    .maxRequestBodyLength(1024 * 2 * 1024 /*2M*/ )

  protected def roleEndpoint(roles: String*) =
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
          .bearer[Option[String]]()
          .securitySchemeName("adminRole")
          .description("sessionId")
      )
      .serverSecurityLogicPure[String, Future] {
        case Some(token) =>
          sessionProvider.session(token) match {
            case Some(user)
              if roles.contains(role => user.roles.contains(role)) =>
              Right(user.userId)
            case Some(_) => Left(PermissionInvalid())
            case None    => Left(TokenInvalid())
          }
        case None => Left(TokenInvalid())
      }
}
