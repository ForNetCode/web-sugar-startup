package com.timzaak
import redis.clients.jedis.JedisPooled
import very.util.web.RedisSession

type Redis = JedisPooled

type UserInfo = Int
type SessionProvider = RedisSession[UserInfo]

//type AdminUserInfo = (String, Set[String]) // (UserId, Roles)
case class AdminUserInfo(userId: String, roles: Set[String])

type AdminSessionProvider = RedisSession[AdminUserInfo]
