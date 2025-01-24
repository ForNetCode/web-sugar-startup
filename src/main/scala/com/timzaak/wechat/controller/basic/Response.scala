package com.timzaak.wechat.controller.basic

import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.server.model.EndpointExtensions.*

sealed trait ErrorInfo
//case class NotFound(msg: String = "Not Exists") extends ErrorInfo
case class BadRequest(msg: String) extends ErrorInfo
case class Unauthorized(msg: String = "Invalid Authorization") extends ErrorInfo
case class Forbid(msg:String = "Forbid") extends ErrorInfo
//case class InternalServerError(msg: String) extends ErrorInfo

//case class ErrorInfoCustomize(code: StatusCode, msg: String) extends ErrorInfo

//case object NoContent extends ErrorInfo

def route = endpoint
  .errorOut(
    oneOf[ErrorInfo](
//      oneOfVariant(
//        statusCode(StatusCode.NotFound).and(stringBody.mapTo[NotFound])
//      ),
      oneOfVariant(
        statusCode(StatusCode.BadRequest).and(stringBody.mapTo[BadRequest])
      ),
      oneOfVariant(
        statusCode(StatusCode.Unauthorized).and(stringBody.mapTo[Unauthorized])
      ),
      oneOfVariant(
        statusCode(StatusCode.Forbidden).and(stringBody.mapTo[Forbid])
      )
//      oneOfVariant(
//        statusCode(StatusCode.InternalServerError)
//          .and(stringBody.mapTo[InternalServerError])
//      )
    )
  )
  .maxRequestBodyLength(1024 * 2 * 1024 /*2M*/ )
