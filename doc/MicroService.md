## APIGateway
目前有两个选项，底层都基于 Envoy， Higress 和 Envoy APIGateway， 两者都专注于K8S集群，单 Higress 还提供 standalone 部署模式，也有WAF插件，对 Dubbo、Nacos 的支持性也好。

## RPC
Dubbo 3.x 可基于 Grpc 来做，还内置断流、熔断等微服务常用功能，目前没找到竞品。

## Config 和 Service Discovery
国外Consul，国内 Nacos。


