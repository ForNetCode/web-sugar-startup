### Permission
基于 https://casbin.org/ 做即可。 采用的 model 一般是 [ACL with superuser](https://casbin.org/docs/supported-models)，方便理解。
数据库采用 [Jdbc Adapter](https://casbin.org/docs/adapters) 即可，缓存 + 数据库 目前没有集成好的，需要自己写。

Tapir 没有集成，需要基于 [jcasbin](https://github.com/casbin/jcasbin) 自行编写，在 `UserExtractInterceptor` 之后，挂上 `PermissionFilterInterceptor`。


### Keycloak Auth and Permission
keycloak 支持 OpenID + Permission。 
认证走 OIDC JWTToken， Application的负担还比较小（未测试 token refresh 和 黑名单）。若是要定制化：
1. 不使用 email，使用手机号
2. 配合微信OpenID、UnionID
3. 只允许单用户登录

就需要开发插件来解决，插件随着 Keycloak 升级也需要变更。

鉴权方案很多，但实际用的时候，每次都需要http请求拉取权限进行判定，要么就需要在此之上封装缓存。

比对了其它的 SSO，其实相差不大，功能丰富度上还是 keycloak 多，也更难用，尤其对小白来讲。

所以 Keycloak 实际并不好用，还是自行构建个简单的，然后补充一个OAuth2.0插件库比较靠谱。
