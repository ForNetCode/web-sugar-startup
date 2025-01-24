package com.timzaak.controller

import com.timzaak.controller.basic.AdminBasicController
import sttp.tapir.*

trait AuthCtrl extends AdminBasicController {

  def test = rolePoint("admin").get
    .in("admin" / "test")
    .out(stringBody)
    .serverLogicPure(id => _ => Right(id))

}
