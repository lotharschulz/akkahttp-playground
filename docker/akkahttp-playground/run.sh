#!/bin/bash

if [ -z "$JAVA_OPTS" ]; then 
 echo JAVA_OPTS="-Xms64m -Xmx384m"
fi

echo "starting with java opts: $JAVA_OPTS"

java $JAVA_OPTS -jar /akkahttp-playground/uberjar.jar
# java -Xms1024m -Xmx2048m -jar ../../../target/scala-2.11/uberjar.jar
# java -Xms1024m -Xmx2048m -jar /akkahttp-playground/uberjar.jar
