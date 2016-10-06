# akka http playground

### test
```
sbt [clean] test
```

### run 
```
sbt "run-main info.lotharschulz.MyService"
```

### docker create image
```
sbt docker:publishLocal
```

#### create & push docker base image java 8
create:
```
cd base/docker/java
docker build -t lotharschulz/java08:[tag] -f Dockerfile .
docker build -t lotharschulz/java08:0.0.1 -f Dockerfile .

```

push:
```
docker push lotharschulz/java08:[tag]
docker push lotharschulz/java08:0.0.1
```

#### create & push docker base image scala
create:
```
cd base/docker/scala
docker build -t lotharschulz/scala:[tag] -f Dockerfile .
docker build -t lotharschulz/scala:0.0.1 -f Dockerfile .

```

push:
```
docker push lotharschulz/scala:[tag]
docker push lotharschulz/scala:0.0.1
```