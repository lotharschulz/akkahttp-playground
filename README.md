# akkahttp playground

A sample project based on akkahttp, docker and minikube. This allows you to develop, debug deploy to a kubernetes environment on your local machine.


[![Build Status](https://travis-ci.org/lotharschulz/akkahttp-playground.svg?branch=master)](https://travis-ci.org/lotharschulz/akkahttp-playground)
[![Circle CI](https://circleci.com/gh/lotharschulz/akkahttp-playground.svg?style=svg)](https://circleci.com/gh/lotharschulz/akkahttp-playground)
<!-- [![codecov.io](https://codecov.io/github/lotharschulz/akkahttp-playground/coverage.svg?branch=master)](https://codecov.io/github/lotharschulz/akkahttp-playground?branch=master)  -->
<!-- ![codecov.io](http://codecov.io/github/lotharschulz/akkahttp-playground/branch.svg?branch=master)   -->

### sbt variables
default
```
# local docker registry
sbt -DdockerOrganization=info.lotharschulz  -DdockerName=akkahttp-playground -DdockerBImage=localhost:5000/scala:0.0.2  -DdockerRepo=localhost:5000                  -DartefactVersion=0.0.3  -DversionInDocker=0.0.3 [sbt command]
```
others 
```
# docker hub registry
sbt -DdockerOrganization=info.lotharschulz  -DdockerName=akkahttp-playground -DdockerBImage=lotharschulz/scala:0.0.2    -DdockerRepo=lotharschulz                    -DartefactVersion=0.0.3  -DversionInDocker=0.0.3 [sbt command]
# pierone docker registry
sbt -DdockerOrganization=info.lotharschulz  -DdockerName=akkahttp-playground -DdockerBImage=lotharschulz/scala:0.0.2    -DdockerRepo=pierone.stups.zalan.do/automata -DartefactVersion=0.0.3  -DversionInDocker=0.0.3 [sbt command]
# google gcr docker registry
sbt -DdockerOrganization=info.lotharschulz  -DdockerName=akkahttp-playground -DdockerBImage=lotharschulz/scala:0.0.2    -DdockerRepo=gcr.io                          -DartefactVersion=v0.0.3 -DversionInDocker=v0.0.3 -DdockerPackageName=akkahttp-playground-gproj/akkahttp-playground [sbt command]
```


[sbt docker tasks](http://www.scala-sbt.org/sbt-native-packager/formats/docker.html#tasks):
```
docker:stage
docker:publishLocal
docker:publish
```

### test
```
sbt [clean] test
```

### run scala code
```sbt "run-main info.lotharschulz.MyService"```  or ```sbt run```

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
docker run -dit -p 8181:8181 --name akkahttp-playground localhost:5000/akkahttp-playground:0.0.3
sbt docker:publishLocal && docker run -dit -p 8181:8181 --name akkahttp-playground localhost:5000/akkahttp-playground:0.0.3
```

##### test the running service
- ```curl -v http://localhost:8181/``` should return something like ```{"status":"is up"}```  
- ```curl -v http://localhost:8181/hello``` should return something like ```{"msg":"my msg"}```  

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
docker push localhost:5000/akkahttp-playground:0.0.3
```
combined docker create image, push to registry & run
```sbt docker:publishLocal && sbt docker:publish && docker run -dit -p 8181:8181 --name akkahttp-playground localhost:5000/akkahttp-playground:0.0.3```

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

#### get me things
```
kubectl get --help
kubectl get deploy,ev,ing,jobs,petsets,po,pv,rs,svc
```

#### minikube
Minikube is a tool that makes it easy to run Kubernetes locally.

start minikube
```minikube start```
start with local insecure registry flag - not needed for minikube use case
```minikube start --vm-driver="virtualbox" --host-only-cidr "192.168.59.3/24" --insecure-registry=localhost:5000```  

```minikube delete``` to make sure the ```--insecure-registry``` flag is honoured [minikube#604#issuecomment-247813764](https://github.com/kubernetes/minikube/issues/604#issuecomment-247813764)

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

#### steps to deploy to minikube
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
- ```sbt docker:publishLocal && sbt docker:publish && docker run -dit -p 8181:8181 --name akkahttp-playground localhost:5000/akkahttp-playground:0.0.3```
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
    Image:			localhost:5000/akkahttp-playground:0.0.3
...
26s	3s	3	{kubelet minikube}	spec.containers{akkahttpplayground}	Normal	Pulling	pulling image "localhost:5000/akkahttp-playground:0.0.3"
25s	3s	3	{kubelet minikube}	spec.containers{akkahttpplayground}	Normal	Pulled	Successfully pulled image "localhost:5000/akkahttp-playground:0.0.3"
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

- ```minikube delete``` deletes the whole k8s/minikube setup from your local machine

##### blog post
http://www.lotharschulz.info/2016/10/19/akkahttp-docker-kubernetes/

#### gcloud / kubernetes - work in progress

- follow steps to [http://kubernetes.io/docs/hellonode/#create-your-kubernetes-cluster](http://kubernetes.io/docs/hellonode/#create-your-kubernetes-cluster)
  - gcloud brings its own kubernetes, make sure an existing kubernetes does not cause conflicts
- ```export PROJECT_ID="akkahttp-playground-gcproj"```
- ```export CLUSTER_ID="akkahttp-playground-cluster"```
- build gcr image 
  ```
  sbt -DdockerOrganization=info.lotharschulz  -DdockerName=akkahttp-playground -DdockerBImage=lotharschulz/scala:0.0.2    -DdockerRepo=gcr.io   -DartefactVersion=v0.0.3   -DversionInDocker=v0.0.3 -DdockerPackageName=$PROJECT_ID/akkahttp-playground docker:publishLocal
  ```
- run the docker image
  ```
  docker run -it -p 8181:8181 --name akkahttp-playground --rm gcr.io/$PROJECT_ID/akkahttp-playground:v0.0.3
  #docker run -it -p 8181:8181 --name akkahttp-playground --rm gcr.io/akkahttp-playground-gcproj/akkahttp-playground:v0.0.3
  ```
  - test the service: ```curl -v http://localhost:8181/``` or ```curl -v http://localhost:8181/hello```
-  upload to docker registry
  ```
  gcloud docker -- push gcr.io/$PROJECT_ID/akkahttp-playground:v0.0.3
  #gcloud docker -- push gcr.io/akkahttp-playground-gcproj/akkahttp-playground:v0.0.3
  ```
- create cluster
  ```
  gcloud container clusters create $CLUSTER_ID
  #gcloud container clusters create akkahttp-playground-cluster
  ```
- list the available clusters (delete cluster)
  ```
  gcloud container clusters list
  # gcloud container clusters delete $CLUSTER_ID
  ```
- get cluster credentials for pod creation
  ```
  gcloud container clusters get-credentials $CLUSTER_ID
  #gcloud container clusters get-credentials akkahttp-playground-cluster
  ```
- create pods
  ```
  kubectl create -f gcloud-pod-config.yaml
  ```
  
- get information about the pod(s)akkahttpplayground-pod (delete pod)
  ```
  kubectl get pods --show-labels
  kubectl describe -f gcloud-pod-config.yaml
  # kubectl delete -f gcloud-pod-config.yaml
  ```

- kubectl logs & events:
  ```
  kubectl logs akkahttpplayground-pod
  kubectl get events
  ```

- create kubectl deployment
  ```
  kubectl create -f gcloud-deployment-config.yaml --record
  # kubectl run akkahttpplayground-pod --image=push gcr.io/$PROJECT_ID/akkahttp-playground:v0.0.3 --port=8181
  # kubectl delete deployment akkahttpplayground-deployment
  ```
  
- kubectl get/describe/scale deployments & replica sets
  ```
  kubectl get deployments
  kubectl describe deployment
  kubectl get rs
  kubectl rollout status deployment/akkahttpplayground-deployment
  kubectl rollout history deployment/akkahttpplayground-deployment
  kubectl scale deployment akkahttpplayground-deployment --replicas 4
  kubectl autoscale deployment akkahttpplayground-deployment --min=1 --max=3 --cpu-percent=80
  ```

- kubectl service option 1
  ```
  kubectl expose deployment akkahttpplayground-deployment --type="LoadBalancer" --port=8181 --target-port=8181
  kubectl get services akkahttpplayground-deployment
  kubectl get svc akkahttpplayground-deployment
  kubectl get svc akkahttpplayground-deployment -o json
  kubectl describe services akkahttpplayground-deployment
  kubectl describe svc akkahttpplayground-deployment
  kubectl delete service akkahttpplayground-deployment
 
  ```
  - check out the service
  ```
  curl -v http://104.155.14.52:8181/hello
  ```
  sample output
  ```$ curl -v http://104.155.14.52:8181/hello
     *   Trying 104.155.14.52...
     * Connected to 104.155.14.52 (104.155.14.52) port 8181 (#0)
     > GET /hello HTTP/1.1
     > Host: 104.155.14.52:8181
     > User-Agent: curl/7.47.0
     > Accept: */*
     > 
     < HTTP/1.1 200 OK
     < Server: akka-http/2.4.10
     < Date: Tue, 20 Dec 2016 22:26:07 GMT
     < Content-Type: application/json
     < Content-Length: 16
     < 
     * Connection #0 to host 104.155.14.52 left intact
     {"msg":"my msg"}```

  
- kubectl service option 2
  ```
  kubectl create -f gcloud-service-config.yaml
  kubectl get services akkahttpplayground-service
  kubectl get svc akkahttpplayground-service
  kubectl get svc akkahttpplayground-service -o json
  kubectl delete service akkahttpplayground-service
  ```
  
  - ingress
  ```
  kubectl create -f gcloud-ingress-config.yaml
  kubectl get ing akkahttpplayground-ingress --watch
  kubectl describe ing akkahttpplayground-ingress
  ```
  sample output
  ```
  NAME                         HOSTS     ADDRESS   PORTS     AGE
  akkahttpplayground-ingress   *                   80        6s
  NAME                         HOSTS     ADDRESS          PORTS     AGE
  akkahttpplayground-ingress   *         130.211.19.252   80        31s
  akkahttpplayground-ingress   *         130.211.19.252   80        31s
  ```


#### uberjar - docker images
- create & run uber jar
  ```
  sbt -DuberjarName=uberjar.jar assembly
  java -Xms1024m -Xmx2048m -jar target/scala-2.11/uberjar.jar 
  ```  
  
- create docker image w/ uberjar 4 pierone
  ```
  cp target/scala-2.11/uberjar.jar docker/akkahttp-playground/uberjar.jar && \ 
  docker build --rm -t pierone.stups.zalan.do/automata/akkahttp-playground:0.0.11 docker/akkahttp-playground && \
  docker push pierone.stups.zalan.do/automata/akkahttp-playground:0.0.11 && \
  rm docker/akkahttp-playground/uberjar.jar
  ```

- create docker image w/ uberjar 4 gcloud
  ```
  cp target/scala-2.11/uberjar.jar docker/akkahttp-playground/uberjar.jar && \ 
  docker build --rm -t gcr.io/$PROJECT_ID/akkahttp-playground:v0.0.3.1 docker/akkahttp-playground && \
  gcloud docker push gcr.io/$PROJECT_ID/akkahttp-playground:v0.0.3.1 && \
  rm docker/akkahttp-playground/uberjar.jar
  ```
  
- run pierone docker container locally
  ```
  # interactive mode 'i' and container gets deleted after crtl C
  docker run -it -p 8181:8181 --name akkahttp-playground --rm pierone.stups.zalan.do/automata/akkahttp-playground:0.0.11
  # deamon 'd'
  docker run -d -p 8181:8181 --name akkahttp-playground pierone.stups.zalan.do/automata/akkahttp-playground:0.0.11  

