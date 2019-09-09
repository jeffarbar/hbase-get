FROM jeffersonfarias/ubuntu-jdk8

MAINTAINER Jefferson Farias

WORKDIR /

RUN mkdir -p /app/

RUN chmod 755 -R /app/

ADD /target/*.jar /app/hbaseGet.jar

RUN chmod 755 -R /app/hbaseGet.jar

ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

EXPOSE 1191

CMD ["java -jar hbaseGet.jar"]
