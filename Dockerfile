FROM adoptopenjdk:11.0.6_10-jdk-hotspot as build
WORKDIR /project
COPY gradlew ./
COPY ./gradle ./gradle
RUN ./gradlew --no-daemon --version --quiet # Download gradle itself
COPY build.gradle settings.gradle ./
RUN ./gradlew --no-daemon --quiet build # Download project dependencies
COPY ./src ./src
RUN ./gradlew --no-daemon --quiet build

FROM adoptopenjdk:11.0.6_10-jre-hotspot
WORKDIR /app
COPY --from=build /project/build/libs/fam-gradle.jar .
COPY ./scripts ./scripts
