# Usa una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR de la aplicación al contenedor
COPY build/libs/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# Expone el puerto que la aplicación usa (puerto por defecto de Spring Boot)
EXPOSE 8080

# Ejecuta la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "demo.jar"]
