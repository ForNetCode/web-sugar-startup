package com.timzaak.controller.basic

import sttp.model.StatusCode
import very.util.keycloak.{ ErrorResp, TapirOIDCAdapter }
import sttp.tapir.server.model.EndpointExtensions.*
import sttp.tapir.*

import scala.concurrent.Future

sealed trait BusinessError extends ErrorResp

case class BadRequest(msg: String) extends BusinessError

trait AdminBasicController(using oidcAdapter: TapirOIDCAdapter) {
  protected def rolePoint(roles: String*) = oidcAdapter
    .hasRole[Future](roles*)
    .errorOutVariantsPrepend(
      oneOfVariant(
        statusCode(StatusCode.BadRequest).and(stringBody.mapTo[BadRequest]),
      )
    )
    .maxRequestBodyLength(1024 * 2 * 1024 /*2M*/ )
}
