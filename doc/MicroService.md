## APIGateway
目前有两个选项，底层都基于 Envoy， Higress 和 Envoy APIGateway， 两者都专注于K8S集群，但 Higress 还提供 standalone 部署模式，也有WAF插件，对 Dubbo、Nacos 的支持性更好。

## RPC
Dubbo 3.x 可基于 Grpc 来做，还内置断流、熔断等微服务常用功能，目前没找到基于Protobuf的竞品。Spring Cloud 那一套和 Java 深度绑定，不好做多语言。

## Config 和 Service Discovery
国外Consul，国内 Nacos。

## SideCar 模式下的微服务
SideCar 增加的中网络包二次流转开销无法忽略。 RPC 框架的多语言支持与功能迭代最终会胜过 SideCar模式


