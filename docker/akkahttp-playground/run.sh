#!/bin/bash

if [ -z "$JAVA_OPTS" ]; then 
 echo JAVA_OPTS="-Xms1024m -Xmx2048m"
fi

echo "starting with java opts: $JAVA_OPTS"

java $JAVA_OPTS -jar /akkahttp-playground/uberjar.jar