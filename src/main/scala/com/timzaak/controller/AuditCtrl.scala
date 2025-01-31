package com.timzaak.controller

import sttp.tapir.*
import com.timzaak.controller.basic.{ AdminBasicController, Role }
import com.timzaak.dao.Audit
import com.timzaak.Module
import very.util.persistence.ScalaSQLExtra.*
import io.circe.generic.auto.*
import sttp.tapir.generic.auto.*
import very.util.entity.Page

case class AuditListReq(
  module: Option[Module],
  refId: Option[Long],
)

trait AuditCtrl extends AdminBasicController {
  def auditList = roleEndpoint(Role.Admin).get
    .in("audit" / "list")
    .in(
      query[Option[Module]]("module")
        .description("Module Value")
        .example(Some(Module.Order))
        .and(query[Option[Long]]("refId"))
        .mapTo[AuditListReq]
    )
    .in(paging)
    .out(jsonBody[Seq[Audit[scalasql.Sc]]])
    .serverLogicPure(_auditList.andThen(_.tupled))

  import scalasql.*
  import scalasql.PostgresDialect.*

  private def _auditList = (userId: String) =>
    (req: AuditListReq, page: Page) => {
      val result = db.run(
        Audit.select
          .search(v => req.module.map(v.module === _), v => req.refId.map(v.refId === _))
          .page(page)
          .sortBy(_.id)
          .desc
      )
      Right(result).withLeft[very.util.keycloak.ErrorResp]
    }
}
