# akkahttp playground

A sample project based on akkahttp, docker and minikube. This allows you to develop, debug deploy to a kubernetes environment on your local machine.

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
- ```minikube docker-env``` keep that output on clipboard for later use, I got
```
export DOCKER_TLS_VERIFY="1"
export DOCKER_HOST="tcp://192.168.99.100:2376"
export DOCKER_CERT_PATH="/home/lothar/.minikube/certs"
export DOCKER_API_VERSION="1.23
```
- ```eval $(minikube docker-env)```
- ```docker run -d -p 5000:5000 --name registry registry:2```
- ```cd base/docker/java/```
- ```docker build -t localhost:5000/java08:0.0.2 -f Dockerfile . && docker push localhost:5000/java08:0.0.2```
- ```cd ../scala/```
- ```docker build -t localhost:5000/scala:0.0.2 -f Dockerfile . && docker push localhost:5000/scala:0.0.2```
- ```cd ../../..```
- ```sbt docker:publishLocal && sbt docker:publish && docker run -dit -p 8181:8181 --name akkahttp-playground localhost:5000/akkahttp-playground:0.0.1```
- ```kubectl get secrets```
- ```docker login localhost:5000``` should bring up something like:
```
Username (admin): [someuser]
Password: [somepassword]
Login Succeeded
```
- ```kubectl create secret docker-registry myregistrykey --docker-server=localhost:5000 --docker-username=[someuser] --docker-password=[somepassword] --docker-email=[someemail]```
- ```kubectl get secrets```
- ```kubectl create -f pod-config.yaml```
- ```kubectl describe pods/akkahttpplaygroundname``` should print out something like:
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
26s	3s	3	{kubelet minikube}	spec.containers{akkahttpplayground}	Normal	Pulling	pulling image "localhost:5000/akkahttp-playground:0.0.1"
25s	3s	3	{kubelet minikube}	spec.containers{akkahttpplayground}	Normal	Pulled	Successfully pulled image "localhost:5000/akkahttp-playground:0.0.1"
3s	3s	1	{kubelet minikube}	spec.containers{akkahttpplayground}	Normal	Created	Created container with docker id 5ac511bf78d3; Security:[seccomp=unconfined]
3s	3s	1	{kubelet minikube}	spec.containers{akkahttpplayground}	Normal	Started	Started container with docker id 5ac511bf78d3
```
- ```kubectl get po```
- ```kubectl logs akkahttpplaygroundname``` should print this:
```
Server online at http://localhost:8181/hello
Hit ENTER to stop...
```
- now run ```curl -v http://192.168.99.100:8181/hello``` to access the akkahttp service  
The outpup should be similar to
```
Hostname was NOT found in DNS cache
 Trying 192.168.99.100...
Connected to 192.168.99.100 (192.168.99.100) port 8181 (#0)
> GET /hello HTTP/1.1
> User-Agent: curl/7.35.0
> Host: 192.168.99.100:8181
> Accept: */*
>
< HTTP/1.1 200 OK
* Server akka-http/2.4.10 is not blacklisted
< Server: akka-http/2.4.10
< Date: Tue, 18 Oct 2016 08:34:39 GMT
< Content-Type: application/json
< Content-Length: 16
<
* Connection #0 to host 192.168.99.100 left intact
{"msg":"my msg"}
```  
- Q: why IP ```192.168.99.100``` and not ```localhost``` as the kubectl logs output suggested?
 - A: in order to be able to pull images from the local docker registry, the eval command (3rd in sequence) above was executed.
    This applies for the complete local docker setup. Thats why the service is not available via
    localhost, but via the IP mentioned in eval output.

- ```minikube delete``` to delete the whole k8s/minikube setup from your local machine

### blog post
to come: http://www.lotharschulz.info/2016/10/13/akkahttp-docker-kubernetes/
