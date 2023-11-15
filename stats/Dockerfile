FROM amazoncorretto:11

COPY stats-service/target/*.jar /stats_service.jar

ENTRYPOINT ["java", "-jar", "/stats_service.jar"]