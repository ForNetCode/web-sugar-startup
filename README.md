# web-sugar-startup
This is a startup template for [web-sugar](https://github.com/ForNetCode/web-sugar)
## Quick Start
```shell
git clone --recursive git@github.com:ForNetCode/web-sugar-startup.git
cd web-sugar-startup
sbt run
```



`com.timzaak.Server` is the entry point. It inits web server and grpc server.
`com.timzaak.DI` init all class, It's [`cake pattern`](https://www.baeldung.com/scala/cake-pattern), you can use macwire
to do the same thing.

## OpenAPI
We use `redoc` to viewer OpenAPI. 

viewer url is: `http://127.0.0.1:8080/docs/index.html`

yaml file url is: `http://127.0.0.1:8080/docs/docs.yaml` 


## OpenTelemetry
It's an example of how to use openTelemetry java agent in IDEA, should not be used in production environment.
### download OpenTelemetry-java-instrumentation
```shell
cd agent
 wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.32.0/opentelemetry-javaagent.jar
```
### add the following to VM Options
```shell
-javaagent:agent/opentelemetry-javaagent.jar -Dotel.javaagent.configuration-file=agent/config.properties
```
more info refer this doc: https://opentelemetry.io/docs/instrumentation/java/automatic/


