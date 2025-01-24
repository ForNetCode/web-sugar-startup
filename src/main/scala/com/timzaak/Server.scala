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
import sttp.tapir.server.interceptor.cors.{ CORSConfig, CORSInterceptor }
import sttp.tapir.redoc.bundle.RedocInterpreter
import sttp.tapir.server.ServerEndpoint

import scala.concurrent.duration.*

@main def app() = {

  import DI.*
  val routes: List[ServerEndpoint[Any, Future]] = List(
    endpoint.get
      .in("ping")
      .out(stringBody)
      .serverLogicSuccessPure(_ => "pong"),
    authCtrl.test,
    authCtrl.session,
  )

  val swaggerRoute = NettyFutureServerInterpreter().toRoute(
    RedocInterpreter()
      .fromEndpoints[Future](routes.map(_.endpoint), "Amiro API", "1.0")
  )

  // https://github.com/softwaremill/tapir/issues/3225
  val serverConfig = NettyConfig.default
    .port(DI.config.getInt("server.web.port"))

  val serverOptions = NettyFutureServerOptions.default.prependInterceptor(
    CORSInterceptor.customOrThrow(CORSConfig.default)
  )

  val serverBinding =
    Await.result(
      NettyFutureServer(serverOptions, serverConfig)
//        .addEndpoint(
//          endpoint.get
//            .in("ping")
//            .out(stringBody)
//            .serverLogicSuccessPure(_ => "pong")
//        )
        .addEndpoints(routes)
        .addRoute(swaggerRoute)
        .start(),
      Duration.Inf
    )

  val port = serverBinding.port
  val host = serverBinding.hostName
  println(s"Server started at port = ${serverBinding.port}")
}
