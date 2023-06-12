import sbt.*

val scala3Version = "3.3.0"



Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

//Compile / PB.protoSources += file("../protobuf")

lazy val webSugar = RootProject(file("./web-sugar"))

lazy val root = project
  .in(file("."))
  .settings(
    name := "web-sugar-startup",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.eclipse.jetty" % "jetty-webapp" % "11.0.15" % "container;compile",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  ).enablePlugins (ScalatraPlugin).enablePlugins(JavaAppPackaging).dependsOn(webSugar)
