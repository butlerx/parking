FROM openjdk:8
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN javac Parking.java
CMD ["java", "Parking"]
