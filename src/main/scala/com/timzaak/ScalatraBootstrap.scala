package com.timzaak

import org.scalatra.LifeCycle
import jakarta.servlet.ServletContext
import very.util.web.PingServlet

import scala.annotation.unused

@unused("used by reflect")
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext): Unit = {
    context mount(PingServlet(), "/ping")
  }

  override def destroy(context: ServletContext): Unit = {
    super.destroy(context)
  }
}
