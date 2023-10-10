package com.timzaak.dao

import io.getquill.*
import io.getquill.context.jdbc.{Decoders, Encoders}
import very.util.persistence.quill.{IDSupport, PageSupport, ZIOJsonSupport}

import java.time.OffsetDateTime

class DB extends PostgresJdbcContext(SnakeCase, "database") with ZIOJsonSupport with IDSupport with PageSupport[SnakeCase] {

  //  there writes MappedEncoding For enum, like the following, but it should be
  //  more simple with inline/macro

  given encodeOffsetDateTime: Encoder[OffsetDateTime] =
    encoder(
      java.sql.Types.TIMESTAMP_WITH_TIMEZONE,
      (index, value, row) => row.setObject(index, value)
    )

  given decodeOffsetDateTime: Decoder[OffsetDateTime] =
    decoder((index, row, _) => row.getObject(index, classOf[OffsetDateTime]))

  //  given encodeNodeType: MappedEncoding[NodeType, Int] = MappedEncoding(
  //    _.ordinal
  //  )
  //  given decodeNodeType: MappedEncoding[Int, NodeType] = MappedEncoding(
  //    NodeType.fromOrdinal
  //  )
}
