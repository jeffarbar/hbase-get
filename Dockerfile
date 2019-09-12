FROM openjdk:8-jre-slim

LABEL author="Jefferson Farias"

USER nobody

ADD /target/*.jar /app/hbaseGet.jar

WORKDIR /app

CMD ["java","-jar","/app/hbaseGet.jar"]

EXPOSE 1191

