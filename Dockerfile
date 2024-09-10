FROM maven:3.8.8 as builder
WORKDIR source
COPY ./ ./
ARG JAR_FILE=entrance/web/target/*.jar
RUN mvn clean package -Dmaven.test.skip=true
RUN cp ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar ./app.jar extract

FROM openjdk:11.0.14
WORKDIR /application
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list
COPY --from=builder source/dependencies/ ./
COPY --from=builder source/snapshot-dependencies/ ./
COPY --from=builder source/spring-boot-loader/ ./
COPY --from=builder source/application/ ./
ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENV MAIN_CLASS="org.springframework.boot.loader.JarLauncher"
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -Djava.security.egd=file:dev/./urandom ${MAIN_CLASS}"]
