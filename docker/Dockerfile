FROM eclipse-temurin:17-jdk-alpine

WORKDIR /server

CMD ["/bin/bash", "/server/bin/app", "-Dconfig.file=/server/config/application.conf", "-Dlogback.configurationFile=/server/config/logback.xml"]
# For Opentelemtry-javaagent
#CMD ["/bin/bash", "/server/bin/app", "-javaagent:/server/agent/opentelemetry-javaagent.jar", "-Dotel.javaagent.configuration-file=/server/config/agent.properties","-Dconfig.file=/server/config/application.conf", "-Dlogback.configurationFile=/server/config/logback.xml"]

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

RUN apk update && apk add --no-cache bash

VOLUME /server/config

COPY ./app /server/