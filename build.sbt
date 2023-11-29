import sbt.*

val scala3Version = "3.3.1"



//Compile / PB.targets := Seq(
//  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
//)

//Compile / PB.protoSources += file("../protobuf")

// change package zip name to app.zip
Universal / packageName := "app"

lazy val webSugar = RootProject(file("./web-sugar"))

lazy val backend = project
  .in(file("."))
  .settings(
    name := "web-sugar-startup",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % "42.6.0",
      "org.eclipse.jetty" % "jetty-webapp" % "11.0.15" % "container;compile",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  ).enablePlugins (ScalatraPlugin).enablePlugins(JavaAppPackaging).dependsOn(webSugar)
