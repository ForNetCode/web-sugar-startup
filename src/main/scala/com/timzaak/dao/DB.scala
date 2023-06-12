package com.timzaak.dao

import io.getquill.*
import io.getquill.context.jdbc.{ Decoders, Encoders }
import very.util.persistence.quill.{ IDSupport, PageSupport, ZIOJsonSupport, OffsetDateTimeSupport }

class DB extends PostgresJdbcContext(SnakeCase, "database") with ZIOJsonSupport with IDSupport with OffsetDateTimeSupport with PageSupport[SnakeCase] {

  //  there writes MappedEncoding For enum, like the following, but it should be
  //  more simple with inline/macro

  //  given encodeNodeType: MappedEncoding[NodeType, Int] = MappedEncoding(
  //    _.ordinal
  //  )
  //  given decodeNodeType: MappedEncoding[Int, NodeType] = MappedEncoding(
  //    NodeType.fromOrdinal
  //  )
}
