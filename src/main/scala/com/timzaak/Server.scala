package com.timzaak

import sttp.tapir.*
import sttp.tapir.server.pekkohttp.PekkoHttpServerInterpreter

import scala.concurrent.Future
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import io.circe.generic.auto.*
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.*
import sttp.apispec.{SecurityScheme, Tag}
import sttp.apispec.openapi.Components
import sttp.tapir.EndpointInput.AuthInfo
import sttp.tapir.Schema.annotations.{description, title}
import sttp.tapir.redoc.bundle.RedocInterpreter

import scala.collection.immutable.ListMap
import scala.concurrent.duration.*


@description("Book entity customise description")
case class Book(
                 @description("title customise description")
                 title: String, year: Option[Int])


@main def app() = {
  val actorSystem: ActorSystem = ActorSystem()
  given ActorSystem = actorSystem

  import actorSystem.dispatcher

  val ping = endpoint
    .summary("Health Check")
    .tag("Health")
    .name("Ping Name") // this is for request log
    .description("endpoint description")
    .get
    .in("ping")
    .out(stringBody)
    .serverLogicSuccess(_ => Future.successful("pong"))


  val postBody = endpoint.post.in("body").in(
    jsonBody[Book].description("parameter customise description")
  ).summary("Test Auth")
    .tag("Book")
    .securityIn(auth.apiKey(header[String]("api_key")).securitySchemeName("api_key"))
    .serverSecurityLogicSuccess(_ => Future.successful(()))
    .out(stringBody)
    .serverLogicSuccess(_ => v => Future.successful("year"))

  val swaggerEndpoints = RedocInterpreter()
    .fromServerEndpoints[Future](List(ping, postBody), "My App", "1.0")

  val swaggerRoute = PekkoHttpServerInterpreter().toRoute(List(ping, postBody):::swaggerEndpoints)

  val serverBinding = Http().newServerAt("0.0.0.0", DI.config.getInt("server.web.port"))
    .bindFlow(swaggerRoute)
    .map{_ =>
    println("init server successful")
  }

  //Await.result(serverBinding.transformWith { r => actorSystem.terminate().transform(_ => r) }, 1.minute)
}
