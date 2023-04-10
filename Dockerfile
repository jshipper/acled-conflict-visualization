FROM maven:3.3-jdk-8

WORKDIR /acled-conflict-visualization
COPY . .
RUN mvn clean install -DskipTests

WORKDIR rest-services
CMD mvn spring-boot:run
