#Build stage

FROM eclipse-temurin:21-jdk AS builder
#set de directory
WORKDIR /app
#COPY <source> <destination>
COPY . .
RUN ./gradlew clean build -x test

# -> the build stage created a .jar file

# Run stage

FROM eclipse-temurin:21-jdk
#set de directory
WORKDIR /app
#COPY <source> <destination>
COPY --from=builder /app/build/libs/*.jar app.jar

#default command that runs when the container starts.
ENTRYPOINT ["java", "-jar", "app.jar"]