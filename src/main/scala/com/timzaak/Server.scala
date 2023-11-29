package com.timzaak

import sttp.tapir.*
import sttp.tapir.server.netty.{NettyConfig, NettyFutureServer, NettyFutureServerInterpreter, NettyFutureServerOptions}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import io.circe.generic.auto.*
import sttp.tapir.Schema.annotations.{description, title}
import sttp.tapir.server.interceptor.cors.{CORSConfig, CORSInterceptor}
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import scala.concurrent.duration.*


@description("Book entity customise description")
case class Book(
                 @description("title customise description")
                 title: String, year: Option[Int])


@main def app() = {

  val ping = endpoint
    .get
    .in("ping")
    .out(stringBody)

  val postBody = endpoint.post.in("body").in(
    jsonBody[Book].description("parameter customise description")
  ).out(stringBody)


  val myEndpoints: List[AnyEndpoint] = List(ping, postBody)
  val swaggerEndpoints = SwaggerInterpreter().fromEndpoints[Future](myEndpoints, "My App", "1.0")
  val swaggerRoute = NettyFutureServerInterpreter().toRoute(swaggerEndpoints)


  // https://github.com/softwaremill/tapir/issues/3225
  val serverConfig = NettyConfig.defaultNoStreaming
    .port(DI.config.getInt("server.web.port"))
    .copy(requestTimeout = None, socketTimeout = None)

  val serverOptions  = NettyFutureServerOptions.default.prependInterceptor(
    CORSInterceptor.customOrThrow(CORSConfig.default))

  val serverBinding =
    Await.result(
    NettyFutureServer(serverOptions, serverConfig)
    .addEndpoint(ping.serverLogicSuccess(_ => Future.successful("pong")))
    .addEndpoint(postBody.serverLogicSuccess(v => Future.successful("year")))
    .addRoute(swaggerRoute)
    .start(),
      Duration.Inf
    )

  val port = serverBinding.port
  val host = serverBinding.hostName
  println(s"Server started at port = ${serverBinding.port}")

  println("Press ENTER to stop the server...")
  scala.io.StdIn.readLine
  Await.result(serverBinding.stop(), Duration.Inf)

}
