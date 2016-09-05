organization  := "info.lotharschulz"

name := "akkahttp-playground"

scalaVersion := "2.11.8"

version := "1.0-SNAPSHOT"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-Xlint", "-Ywarn-adapted-args", "-Xfatal-warnings", "-feature")

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

libraryDependencies ++= {
  val akkaV      = "2.4.3"
  val scalaTestV = "2.2.6"
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % "2.4.7"
    ,"com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.9"
    //,"com.typesafe.akka" %% "akka-http-experimental" % "2.4.7"
    //,"com.typesafe.akka" %% "akka-actor" % "2.4.0"
    ,"com.typesafe.akka" %% "akka-http-testkit" % akkaV
    ,"org.scalatest" %% "scalatest" % scalaTestV % "test"
  )
}

pomExtra :=
  <scm>
    <url>https://github.com/lotharschulz/armstrongNumbers</url>
    <connection>scm:git:github.com/lotharschulz/armstrongNumbers.git</connection>
    <developerConnection>scm:git:https://github.com/lotharschulz/armstrongNumbers.git</developerConnection>
  </scm>
    <developers>
      <developer>
        <id>lotharschulz</id>
        <name>lothar</name>
        <email>mail[at]lothar[minus]schulz[dot]info</email>
        <url>https://github.com/lotharschulz</url>
      </developer>
    </developers>