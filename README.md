# akka http playground

### test
```
sbt [clean] test
```

### run scala code
```
sbt "run-main info.lotharschulz.MyService"
```

### docker

#### docker create image
```
sbt docker:publishLocal
```

#### docker run local image
```
docker run -dit -p 8181:8181 --name akkahttp-playground lotharschulz/akkahttp-playground:0.0.1
sbt docker:publishLocal && docker run -dit -p 8181:8181 --name akkahttp-playground lotharschulz/akkahttp-playground:0.0.1
```

#### test the running service
```curl http://localhost:8181/hello``` should return something like ```{"msg":"my msg"}```  

#### docker stop local image
```
docker stop akkahttp-playground
```

#### docker remove local image
```
docker rm akkahttp-playground
```

##### create & push docker base image java08
create:
```
cd base/docker/java
docker build -t lotharschulz/java08:[tag] -f Dockerfile .
docker build -t lotharschulz/java08:0.0.2 -f Dockerfile .

```

push:
```
docker push lotharschulz/java08:[tag]
docker push lotharschulz/java08:0.0.2
```

##### create & push docker base image scala
create:
```
cd base/docker/scala
docker build -t lotharschulz/scala:[tag] -f Dockerfile .
docker build -t lotharschulz/scala:0.0.2 -f Dockerfile .

```

push:
```
docker push lotharschulz/scala:[tag]
docker push lotharschulz/scala:0.0.2
```
