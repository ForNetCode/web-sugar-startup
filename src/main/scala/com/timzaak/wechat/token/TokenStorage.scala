package com.timzaak.wechat.token

import com.timzaak.Redis

trait TokenStorage {
  def token: String
  def setToken(token: String): Unit
}
class LocalTokenStorage extends TokenStorage {
  private var _token: String = ""
  def token: String = _token

  def setToken(token: String): Unit = _token = token
}
class RedisTokenStorage(redis: Redis, key: String = "wxToken")
  extends TokenStorage {
  def token: String = redis.get(key) // must not be null
  def setToken(token: String): Unit = redis.set(key, token)
}
