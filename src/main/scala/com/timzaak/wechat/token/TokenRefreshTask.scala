package com.timzaak.wechat.token

import cn.binarywang.wx.miniapp.api.WxMaService
import org.quartz.SimpleScheduleBuilder
import very.util.task.executor.singleTaskExecutor
import very.util.task.{ QuartzManager, UnitTask }
import very.util.web.LogSupport

import scala.util.{ Try, Success }

class TokenRefreshTask(wxMaService: WxMaService, storage: TokenStorage)
  extends UnitTask
  with LogSupport {
  override def name: String = "WxTokenRefresh"

  override def run(param: Unit)(using context: Unit): Unit = {
    LazyList
      .from(0, 1)
      .take(5)
      .map { number =>
        val result = Try { wxMaService.getAccessToken(true) }
        result.failed.foreach { error =>
          logger.warn(s"getToken failure,number: $number", error)
        }
        result
      }
      .collectFirst {
        case Success(token) if token != null =>
          logger.debug(s"getToken:$token")
      }
  }
}

object TokenRefreshTask {
  def schedule(
    quartzManager: QuartzManager,
    tokenRefreshExecutor: TokenRefreshTask
  ): Unit = quartzManager.schedulerTask(
    singleTaskExecutor(tokenRefreshExecutor)(using {}),
    tokenRefreshExecutor.name,
    () => {},
    SimpleScheduleBuilder
      .simpleSchedule()
      .withIntervalInMinutes(110)
      .repeatForever()
  )
}
