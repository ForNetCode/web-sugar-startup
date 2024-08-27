### Permission
基于 https://casbin.org/ 做即可。 采用的 model 一般是 [ACL with superuser](https://casbin.org/docs/supported-models)，方便理解。
数据库采用 [Jdbc Adapter](https://casbin.org/docs/adapters) 即可，缓存 + 数据库 目前没有集成好的，需要自己写。

Tapir 没有集成，需要基于 [jcasbin](https://github.com/casbin/jcasbin) 自行编写，在 `UserExtractInterceptor` 之后，挂上 `PermissionFilterInterceptor`。


