FROM eclipse-temurin:17-jammy
WORKDIR compiler
ARG JAR_FILE=target/osm-nds-live-compiler-*-jar-with-dependencies.jar
ARG CONFIG_FILE=src/main/resources/docker-application.conf
COPY ${JAR_FILE} compiler.jar
COPY ${CONFIG_FILE} application.conf
COPY src/main/resources/mapping mapping
ENTRYPOINT java -Dconfig.file=application.conf $JAVA_OPS -jar compiler.jar