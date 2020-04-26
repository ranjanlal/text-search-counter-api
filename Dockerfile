FROM adoptopenjdk/openjdk11:ubi
RUN mkdir /opt/app
COPY . /opt/app/
WORKDIR /opt/app/
CMD ["./mvnw", "spring-boot:run"]
