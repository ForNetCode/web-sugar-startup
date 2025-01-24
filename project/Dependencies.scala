import sbt.*
object Dependencies {
  lazy val wxLib = Seq(
    "com.github.binarywang" % "weixin-java-pay" % "4.7.0",
    "com.github.binarywang" % "weixin-java-miniapp" % "4.7.0",
  )
}
