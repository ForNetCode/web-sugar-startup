package com.timzaak.service.task

import com.timzaak.service.OrderService
import org.quartz.SimpleScheduleBuilder
import very.util.task.executor.singleTaskExecutor
import very.util.task.{QuartzManager, UnitTask}
import very.util.web.LogSupport

class OrderExpireTask(orderService: OrderService) extends UnitTask with LogSupport {
  override def name: String = "OrderExpire"

  override def run(param: Unit)(using context: Unit): Unit = {
    orderService.expireOrder()
  }
}
object OrderExpireTask {
  def schedule(orderService: OrderService)(using quartzManager: QuartzManager) = {
    val orderExpireTask = OrderExpireTask(orderService)
    quartzManager.schedulerTask(
      singleTaskExecutor(orderExpireTask)(using {}),
      orderExpireTask.name,
      () => {},
      SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(30).repeatForever()
    )
  }
}
