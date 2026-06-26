FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY target/student-system-server-1.0.0.jar app.jar

EXPOSE 8080

ENV PORT=8080
ENV DB_DRIVER=com.mysql.cj.jdbc.Driver
ENV DB_URL=jdbc:mysql://host.docker.internal:3306/student_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
ENV DB_USER=root
ENV DB_PASS=root

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
