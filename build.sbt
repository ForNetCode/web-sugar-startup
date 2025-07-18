import sbt.*

val scala3Version = "3.7.1"

//Compile / PB.targets := Seq(
//  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
//)

//Compile / PB.protoSources += file("../protobuf")

// change package zip name to app.zip
Universal / packageName := "app"
// skip package doc files
compile / packageDoc / mappings := Seq()

// delete all bin except server
Universal / mappings := {
  val universalMappings = (Universal / mappings).value
  universalMappings filter { case (_, name) =>
    !(name.startsWith("bin/") && name != "bin/app")
  }
}

// exclude config files from jar
unmanagedResources / excludeFilter := "*.conf"

lazy val webSugar = RootProject(file("./web-sugar"))

lazy val backend = project
  .in(file("."))
  .settings(
    name := "web-sugar-startup",
    version := "0.1.0",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % "42.7.7",
      "org.casbin" % "jcasbin" % "1.82.0",
      "org.scalameta" %% "munit" % "1.1.1" % Test
    ),
    // Compile / mainClass := Some("com.timzaak.app")
  )
  .enablePlugins(JavaServerAppPackaging)
  .dependsOn(webSugar)
