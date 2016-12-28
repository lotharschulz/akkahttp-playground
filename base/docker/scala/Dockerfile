# Scala based on Java 8

FROM lotharschulz/java08:0.0.3
MAINTAINER Lothar Schulz <mail@lothar-schulz.info>

ENV SCALA_VERSION 2.11.8
ENV SCALA_DEB http://www.scala-lang.org/files/archive/scala-$SCALA_VERSION.deb
ENV SBT_VERSION 0.13.12
ENV SBT_DEB http://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb

RUN \
    echo "==> Install curl helper tool..."                                        && \
    apt-get -qq update                                                            && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y -qq --allow-downgrades curl && \
    \
    \
    \
    echo "===> install from Typesafe repo (contains old versions but they have all dependencies we need later on)"  && \
    curl -sSL http://apt.typesafe.com/repo-deb-build-0002.deb -o repo-deb.deb  && \
    dpkg -i repo-deb.deb                                                       && \
    apt-get -qq update                                                         && \
    \
    \
    \
    echo "===> install Scala"                                && \
    DEBIAN_FRONTEND=noninteractive \
        apt-get install -y --allow-downgrades libjansi-java  && \
    curl -sSL $SCALA_DEB -o scala.deb                        && \
    dpkg -i scala.deb                                        && \
    \
    \
    \
    echo "===> install sbt"                    && \
    curl -L -o sbt-$SBT_VERSION.deb $SBT_DEB   && \
    dpkg -i sbt-$SBT_VERSION.deb               && \
    rm sbt-$SBT_VERSION.deb                    && \
    apt-get -qq update                         && \
    apt-get install                            && \
    \
    \
    \
    echo "sbt version: "    && \
    sbt about               && \
    echo "java version: "   && \
    java -version           && \
    echo "scala version: "  && \
    scala -version || true

# Define default command.
CMD ["scala"]
