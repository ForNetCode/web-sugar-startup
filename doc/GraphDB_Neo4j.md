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
