package com.timzaak.dao

import io.getquill.*
import very.util.persistence.quill.{IDSupport, PageSupport, CirceJsonSupport}



class DB extends PostgresJdbcContext(SnakeCase, "database") with CirceJsonSupport with IDSupport with PageSupport[SnakeCase]