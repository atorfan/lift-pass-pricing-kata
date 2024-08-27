# Establecer la imagen base de Gradle con JVM 17
FROM gradle:8.5-jdk17 AS build

# Establecer el directorio de trabajo en /app
WORKDIR /app

# Copiar el archivo build.gradle y el directorio src al contenedor
COPY build.gradle.kts /app/build.gradle.kts
COPY src /app/src

# Ejecutar Gradle para compilar la aplicación
RUN gradle build --no-daemon -x test fatJar

# Establecer la imagen base de JDK 17 para ejecutar la aplicación
FROM eclipse-temurin:17-jdk AS runtime

# Establecer el directorio de trabajo en /app
WORKDIR /app

# Copiar el archivo JAR construido desde la etapa anterior
COPY --from=build /app/build/libs/*-all.jar /app/app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
