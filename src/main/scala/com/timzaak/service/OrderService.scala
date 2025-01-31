package com.timzaak.service

import com.timzaak.dao.Order

import java.time.temporal.ChronoUnit
import java.time.OffsetDateTime
import scala.concurrent.duration.*

trait OrderService extends BasicService {

  def expireOrder(expiredTime: Duration = 30.minutes) = {
    val time = OffsetDateTime.now.truncatedTo(ChronoUnit.MINUTES)
    val end = time.minusMinutes(expiredTime.toMinutes)
    val start = end.minusMinutes(1)
    // logger.debug(s"order expire task: $start $end")
    val orderIds = db.run(Order.setOrderExpired(start, end))
    if (orderIds.nonEmpty) {
      logger.info(s"order expired: $start $end, ${orderIds.mkString(",")}")
    } else {
      logger.debug(s"order no expired: $start $end")
    }
  }
}
