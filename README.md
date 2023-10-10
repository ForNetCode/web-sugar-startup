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


