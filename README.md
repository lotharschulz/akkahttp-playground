# akkahttp playground

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

remove all containers:
```docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)```


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
Minikube is a tool that makes it easy to run Kubernetes locally.
```minikube delete``` to make sure the ```--insecure-registry```
flag is honoured (https://github.com/kubernetes/minikube/issues/604#issuecomment-247813764)

start with local insecure registry flag  
```minikube start --vm-driver="virtualbox" --host-only-cidr "192.168.59.3/24" --insecure-registry=localhost:5000```  
check the cluster status    
```kubectl get cs```  
dashboard
```minikube dashboard```   

#### pods
create a pod:  
```kubectl create -f pod-config.yaml```

delete a pod:  
```kubectl delete pod akkahttpplaygroundname```

describe a pod
```kubectl describe pods/akkahttpplaygroundname```

#### secrets

create a secret:  
```kubectl create secret docker-registry myregistrykey --docker-server=localhost:5000 --docker-username=[someuser] --docker-password=[somepassword] --docker-email=[someemail]```

get me secrets:  
```kubectl get secrets```

delete a secret:  
```kubectl delete secret myregistrykey```

## steps to deploy to minikube
- ```minikube start```
- ```eval $(minikube docker-env)```
- ````docker run -d -p 5000:5000 --name registry registry:2```
- ```cd base/docker/java/```
- ```docker build -t localhost:5000/java08:0.0.2 -f Dockerfile .```
- ```docker push localhost:5000/java08:0.0.2```
- ```cd ../scala/```
- ```docker build -t localhost:5000/scala:0.0.2 -f Dockerfile .```
- ```docker push localhost:5000/scala:0.0.2```
- ```cd ../../..```
- ```sbt docker:publishLocal && sbt docker:publish &&
docker run -dit -p 8181:8181 --name akkahttp-playground localhost:5000/akkahttp-playground:0.0.1 ```
- ```kubectl get secrets```
- ```kubectl create secret docker-registry myregistrykey --docker-server=localhost:5000 --docker-username=[someuser] --docker-password=[somepassword] --docker-email=[someemail]```
- ```kubectl get secrets```
- ```kubectl create -f pod-config.yaml```
- ```kubectl describe pods/akkahttpplaygroundname```
- output like:
```
Name:		akkahttpplaygroundname
Namespace:	default
Node:		minikube/192.168.99.100
Start Time:	Thu, 13 Oct 2016 22:41:06 +0200
Labels:		<none>
Status:		Running
IP:		172.17.0.5
...
Containers:
  akkahttpplayground:
    Container ID:		docker://e638961e5dd9db76e30643523b20330afeac752ff76f9468f5cec6dfbd725275
    Image:			localhost:5000/akkahttp-playground:0.0.1
...
14s		14s		1	{kubelet minikube}	spec.containers{akkahttpplayground}	Normal		Started		Started container with docker id 134a080869a2
14s		10s		2	{kubelet minikube}	spec.containers{akkahttpplayground}	Normal		Pulling		pulling image "localhost:5000/akkahttp-playground:0.0.1"
14s		10s		2	{kubelet minikube}	spec.containers{akkahttpplayground}	Normal		Pulled		Successfully pulled image "localhost:5000/akkahttp-playground:0.0.1"
```

### blog post
to come: http://www.lotharschulz.info/2016/10/13/akkahttp-docker-kubernetes/
