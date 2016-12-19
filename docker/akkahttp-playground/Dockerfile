FROM lotharschulz/scala:0.0.2
MAINTAINER Lothar Schulz <mail@lothar-schulz.info>
RUN mkdir -p /akkahttp-playground
WORKDIR /akkahttp-playground

COPY uberjar.jar uberjar.jar
RUN ["chown", "-R", "daemon:daemon", "."]
EXPOSE 8181
USER daemon

ENTRYPOINT ["run.sh"]
CMD []
