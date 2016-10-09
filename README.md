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

The commands below assume you are running a local docker registry as described in https://docs.docker.com/registry/.
- start/run local registry ```docker run -d -p 5000:5000 --name registry registry:2```
- stop & remove local registry ```docker stop registry && docker rm registry```

#### docker create image
```
sbt docker:publishLocal
```

#### docker run local image
```
docker run -dit -p 8181:8181 --name akkahttp-playground localhost:5000/akkahttp-playground:0.0.1
sbt docker:publishLocal && docker run -dit -p 8181:8181 --name akkahttp-playground localhost:5000/akkahttp-playground:0.0.1
```

##### test the running service
```curl http://localhost:8181/hello``` should return something like ```{"msg":"my msg"}```  

###### check some logs
```docker logs -f akkahttp-playground``` will follows the docker container logs. 
Run ```curl http://localhost:8181/hello``` and experience logs like 
```
[my-system-akka.actor.default-dispatcher-4] [akka.actor.ActorSystemImpl(my-system)] HttpMethod(GET) http://localhost:8181/hello: 200 OK 
entity: {"msg":"my msg"}
``` 

#### push to local registry
``` 
docker push localhost:5000/akkahttp-playground:0.0.1
```
combined docker create image, push to registry & run
```sbt docker:publishLocal && sbt docker:publish && docker run -dit -p 8181:8181 --name akkahttp-playground localhost:5000/akkahttp-playground:0.0.1 ```

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
docker build -t localhost:5000/java08:[tag] -f Dockerfile .
docker build -t localhost:5000/java08:0.0.2 -f Dockerfile .

```

push:
```
docker push localhost:5000/java08:[tag]
docker push localhost:5000/java08:0.0.2
```

##### create & push docker base image scala
create:
```
cd base/docker/scala
docker build -t localhost:5000/scala:[tag] -f Dockerfile .
docker build -t localhost:5000/scala:0.0.2 -f Dockerfile .

```

push:
```
docker push localhost:5000/scala:[tag]
docker push localhost:5000/scala:0.0.2
```

### kubernetes

#### minikube
@TODO: link minikube description and install
 
#### create a pod
```kubectl create -f pod-config.yaml```

#### delete a pod
```kubectl delete pod akkahttpplaygroundname```