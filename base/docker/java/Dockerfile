# inspired by https://github.com/William-Yeh/docker-java8/blob/master/Dockerfile

FROM ubuntu:16.04
MAINTAINER Lothar Schulz <mail@lothar-schulz.info>

RUN \
    echo "===> update ubuntu"               && \
    apt-get -qq update                      && \
    apt-get install -y unattended-upgrades  && \
    \
    \
    echo "===> add webupd8 repository..."   && \
    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list  && \
    echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list  && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886  && \
    apt-get -qq update                      && \
    \
    \
    echo "===> install Java"                && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections  && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections  && \
    DEBIAN_FRONTEND=noninteractive  apt-get install -y --allow-downgrades oracle-java8-installer oracle-java8-set-default  && \
    \
    \
    echo "===> clean up..."                 && \
    rm -rf /var/cache/oracle-jdk8-installer && \
    apt-get clean                           && \
    rm -rf /var/lib/apt/lists/*             && \
    \
    \
    echo "java version: "                   && \
    java -version


ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# Define default command.
CMD ["java"]
