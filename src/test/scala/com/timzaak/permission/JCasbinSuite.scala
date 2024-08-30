package com.timzaak.permission

import munit.FunSuite
import org.casbin.jcasbin.main.Enforcer
import org.casbin.jcasbin.model.Model
import org.casbin.jcasbin.persist.file_adapter.FileAdapter

import java.io.ByteArrayInputStream

class JCasbinSuite extends FunSuite {

  def getEnforcer(model:String, csv:String):Enforcer = {
    Enforcer(Model.newModelFromString(model), FileAdapter(ByteArrayInputStream(csv.getBytes)))
  }
  test("basic with root permission") {
    val model = """[request_definition]
                  |r = sub, obj, act
                  |
                  |[policy_definition]
                  |p = sub, obj, act
                  |
                  |[policy_effect]
                  |e = some(where (p.eft == allow))
                  |
                  |[matchers]
                  |m = r.sub == p.sub && r.obj == p.obj && r.act == p.act || r.sub == "root"""".stripMargin
    val csv = """p, alice, data1, read
                |p, bob, data2, write""".stripMargin


    val enforcer = getEnforcer(model, csv)

    assert(enforcer.enforce("alice", "data1", "read"))
    assert(!enforcer.enforce("alice", "data1", "write"))
    assert(enforcer.enforce("root", "data1", "write"))
    assert(enforcer.enforce("root", "data3", "write"))
  }

  test("rbac permission") {
    val enforcer = getEnforcer("""[request_definition]
                                 |r = sub, obj, act
                                 |
                                 |[policy_definition]
                                 |p = sub, obj, act
                                 |
                                 |[role_definition]
                                 |g = _, _
                                 |
                                 |[policy_effect]
                                 |e = some(where (p.eft == allow))
                                 |
                                 |[matchers]
                                 |m = g(r.sub, p.sub) && r.obj == p.obj && r.act == p.act""".stripMargin,
    """p, alice, data1, read
      |p, bob, data2, write
      |p, data2_admin, data2, read
      |p, data2_admin, data2, write
      |g, alice, data2_admin""".stripMargin)
    assert(enforcer.enforce("alice", "data2", "write"))
    assert(enforcer.enforce("alice", "data1", "read"))
  }
}
