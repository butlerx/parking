# parking

## Docker
Compile in docker
```
docker run --rm -v "$PWD":/usr/src/myapp -w /usr/src/myapp openjdk:8 javac Parking.java
```
Running in docker
```
docker run --rm -v "$PWD":/usr/src/myapp -w /usr/src/myapp openjdk:8 java Parking
```
