## Neo4j
### Installation

快速体验只需要安装客户端。

客户端参考：https://neo4j.com/deployment-center/?desktop-gdb ，需要填写个人信息。

或者用服务器端提供的Web页面， 端口：7474

服务器端参考： https://neo4j.com/docs/operations-manual/current/docker/introduction/
```shell
docker run -it --rm \
  --volume=/home/amiro/db/neo4j/data:/data \
  --name neo4j \
  --publish=7474:7474 --publish=7687:7687 \
  --env NEO4J_AUTH=neo4j/your_password \
  --env NEO4J_PLUGINS='["apoc"]' \
  neo4j:5.23.0
```

### Scala Client

https://neotypes.github.io/neotypes/ 依赖了shapeless，但提供很好的语法糖。

## ElasticSearch
客户端： https://github.com/Philippus/elastic4s ，有较好的json库适配。
Java Client API已经函数化，但 Json 序列化强绑定，需要在 Jackson 上做文章，需要额外改写。

考虑到易读性，最好还是引入 [Jsonnet](https://jsonnet.org/) 做复杂 Json 请求参数生成，会更易读一些。

理解ES可参考： https://github.com/timzaak/blog/issues/88


### CDC to ES
Debezium 是一种较高成本方案，还有一种就是手动定时查变更数据，手动改ES，虽然慢点，但初期基本够用，一般5分钟延迟即可
