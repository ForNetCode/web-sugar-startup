## APIGateway
目前有两个选项，Higress 和 Envoy APIGateway，底层都基于 Envoy， 两者都专注于K8S集群，但 Higress 还提供 standalone 部署模式，也有WAF插件，对 Dubbo、Nacos 的支持性更好。

## RPC
Dubbo 3.x 可基于 Grpc 来做，Java 版本内置断流、熔断等微服务常用功能，可以看作包含微服务治理的GRPC。 Dubbo 的其余编程语言重心主要在Go上，Rust 和 NodeJS 功能还较为孱弱，相比于开源方案叠加了 Nacos 集成 和 简单路由控制。

## Config 和 Service Discovery
国外Consul，国内 Nacos，国内云平台会提供 Nacos 

## Trace
走 OpenTelemetry，各大云平台都有完整适配，在 Java 生态里，OpenTelemetry 可以走 Java Agent，对业务代码无入侵。
## Log
各大云平台都有 Log 日志采集方案，一般解决方案是写本地 + 转发，或者日志框架直接网络写Log Collector。 第一种方案会好一些，在k8s上，往往会挂载一个日志落盘转发镜像，业务无入侵。

云平台会提供各种集成方案，直接基于它们的方案做就好。


## SideCar 模式下的微服务
SideCar 提供了中间件下沉的方案，例如日志收集、网络透明化等，业务代码无入侵。但增加的网络包二次流转开销无法忽略。 RPC 框架的多语言支持与功能迭代最终会胜过 SideCar 模式。
