### Permission
基于 https://casbin.org/ 做即可。 采用的 model 一般是 [ACL with superuser](https://casbin.org/docs/supported-models)，方便理解。
数据库采用 [Jdbc Adapter](https://casbin.org/docs/adapters) 即可，缓存 + 数据库 目前没有集成好的，需要自己写。

Tapir 没有集成，需要基于 [jcasbin](https://github.com/casbin/jcasbin) 自行编写，在 `UserExtractInterceptor` 之后，挂上 `PermissionFilterInterceptor`。
casbin 还开源了 casdoor 项目，类比于 keycloak。


### Keycloak Authentication and Authorization
keycloak 支持 OpenID、SAML2、OAuth2 等认证方案。 
若是要定制化：
1. 不使用 email，使用手机号，短信验证
2. 配合微信OpenID、UnionID

就需要开发插件来解决，插件随着 Keycloak 升级也需要变更。

keycloak 提供 policy enforcer 鉴权方案，但实际用的时候，policy enforcer 封装并不友好，需要重新实现或者移植 keycloak4s-auth。
可以通过 realm role 来简单做。

集成可以基于 pac4j 来做，Keycloak 官方已不再支持 keycloak-java-adapter。
对于前后端分离的项目，最简单的 Auth 对接方案
1. 在keycloak上，针对前端创建 clientId, `Client authentication` 为 Off。清理 `Client scopes` 只保留 roles、{clientId}-dedicated、profile。
2. 前端引入 keycloak-js package，并将从 keycloak 获取的 Token 作为 `Authorization: Bearer {Token}` 传递后端。
3. 后端通过 {keycloak-base-url}/realms/{realm}/protocol/openid-connect/certs 获取 JWKS, 并进行校验。可获得 OpenId + Role

针对 Token 使用优化点可以有：
1. 后端 增加 Token => (OpenId, roles) Cache
2. 后端依据 Token 获取信息验证后，重新生成 sessionId => (OpenId,  roles) 映射，前端只需要使用SessionId 即可。

参考： 
[Keycloak 插件开发系列](https://github.com/kavahub/keycloak?tab=readme-ov-file)
[keycloak quick start spring](https://github.com/keycloak/keycloak-quickstarts/tree/latest/spring/rest-authz-resource-server)


### Keycloak 运行
```shell
docker run --name keycloak \
-e KEYCLOAK_ADMIN=admin \
-e KEYCLOAK_ADMIN_PASSWORD=admin \
-p 8180:8180 \
keycloak/keycloak:26.1 \
start-dev \
--http-port=8180
```
