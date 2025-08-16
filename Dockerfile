FROM openjdk:21
ADD target/loomweb.jar loomweb.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "loomweb.jar"]
# Use the following command to build the Docker image/container:
# docker build -t loomweb:latest .
# Use the following command to run the Docker container:
# docker run -p 8080:8080 loomweb:latest        