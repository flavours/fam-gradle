FROM adoptopenjdk:11.0.6_10-jdk-hotspot as build
WORKDIR /fam-gradle
COPY gradlew ./
COPY ./gradle ./gradle
RUN ./gradlew --no-daemon --version --quiet # Download gradle itself
COPY build.gradle settings.gradle ./
RUN ./gradlew --no-daemon --quiet build # Download project dependencies
COPY ./src ./src
RUN ./gradlew --no-daemon --quiet installDist

FROM adoptopenjdk:11.0.6_10-jre-hotspot
WORKDIR /bin/fam-gradle
COPY --from=build /fam-gradle/build/install/fam-gradle .
COPY ./scripts .
RUN ln -s /bin/fam-gradle/add /bin/add && \
    ln -s /bin/fam-gradle/check /bin/check && \
    ln -s /bin/fam-gradle/remove /bin/remove
ENV FAM_IDENTIFIER flavour/fam-gradle:0.0.1
