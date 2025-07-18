package com.timzaak

import sttp.tapir.*
import sttp.tapir.server.netty.{
  NettyConfig,
  NettyFutureServer,
  NettyFutureServerInterpreter,
  NettyFutureServerOptions
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Await, Future }
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import io.circe.generic.auto.*
import sttp.apispec.{ SecurityScheme, Tag }
import sttp.apispec.openapi.Components
import sttp.tapir.EndpointInput.AuthInfo
import sttp.tapir.Schema.annotations.{ description, title }
import sttp.tapir.server.interceptor.cors.{ CORSConfig, CORSInterceptor }
import sttp.tapir.redoc.bundle.RedocInterpreter

import scala.collection.immutable.ListMap
import scala.concurrent.duration.*

@description("Book entity customise description")
case class Book(
  @description("title customise description")
  title: String,
  year: Option[Int]
)

@main def app() = {

  val ping = endpoint
    .summary("Health Check")
    .tag("Health")
    .name("Ping Name") // does not work
    .description("endpoint description")
    .get
    .in("ping")
    .out(stringBody)
    .serverLogicSuccess(_ => Future.successful("pong"))

  val postBody = endpoint.post
    .in("body")
    .in(
      jsonBody[Book].description("parameter customise description")
    )
    .summary("Test Auth")
    .securityIn(
      auth
        .apiKey(header[String]("api_key"))
        .copy(info = AuthInfo.Empty.securitySchemeName("api_key"))
    )
    .serverSecurityLogicSuccess(_ => Future.successful(()))
    .out(stringBody)
    .serverLogicSuccess(_ => v => Future.successful("year"))

  val swaggerEndpoints = RedocInterpreter(
    customiseDocsModel = openAPI => {
      openAPI
        .tags(List(Tag("Health", description = Some("Tag Description"))))
        .components(
          openAPI.components.get.copy(
            securitySchemes = ListMap(
              "api_key" -> Right(
                SecurityScheme(
                  "apiKey",
                  description = None,
                  name = Some("api_key"),
                  in = Some("header"),
                  scheme = None,
                  bearerFormat = None,
                  flows = None,
                  openIdConnectUrl = None,
                )
              )
            )
          )
        )
      /*
        .components(Components(securitySchemes = ListMap("api_key" -> Right(SecurityScheme(
          "apiKey",
          description = None,
          name = Some("api_key"),
          in = Some("header"),
          scheme = None,
          bearerFormat = None,
          flows = None, openIdConnectUrl = None,
        )))))*/
    }
  )
    .fromServerEndpoints[Future](List(ping, postBody), "My App", "1.0")

  val swaggerRoute = NettyFutureServerInterpreter().toRoute(swaggerEndpoints)

  // https://github.com/softwaremill/tapir/issues/3225
  val serverConfig = NettyConfig.default
    .port(DI.config.getInt("server.web.port"))

  val serverOptions = NettyFutureServerOptions.default.prependInterceptor(
    CORSInterceptor.customOrThrow(CORSConfig.default)
  )

  val serverBinding =
    Await.result(
      NettyFutureServer(serverOptions, serverConfig)
        .addEndpoints(List(ping, postBody))
        .addRoute(swaggerRoute)
        .start(),
      Duration.Inf
    )

  val port = serverBinding.port
  val host = serverBinding.hostName
  println(s"Server started at port = ${serverBinding.port}")
}
