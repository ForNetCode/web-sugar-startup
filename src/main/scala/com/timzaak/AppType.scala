package com.timzaak
import redis.clients.jedis.JedisPooled
import very.util.web.RedisSession

type Redis = JedisPooled

type UserInfo = Int
type SessionProvider = RedisSession[UserInfo]

type AdminUserInfo = (String, Set[String]) // (UserId, Roles)
type AdminSessionProvider = RedisSession[UserInfo]
