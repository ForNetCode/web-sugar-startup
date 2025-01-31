package com.timzaak.service

import very.util.persistence.DBHelper
import very.util.web.LogSupport

trait BasicService(using protected val db: DBHelper) extends LogSupport
