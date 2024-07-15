package com.timzaak.dao

import very.util.config.WithConfig
import scalikejdbc.{AutoSession, DBSession}
import scalikejdbc.config.DBs

trait DB extends WithConfig {
  DBs.setup()

  given session: DBSession = AutoSession
}