FROM alpine/java:21-jdk

WORKDIR /db

COPY h2.jar /db/h2.jar

RUN mkdir /db/data

ENTRYPOINT [ "java", "-cp", "h2.jar", "org.h2.tools.Server", "-web", "-webAllowOthers", "-tcp", "-tcpAllowOthers","-ifNotExists", "-baseDir", "/db/data"]