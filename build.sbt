organization  := "info.lotharschulz"

name := "akkahttp-playground"

scalaVersion := "2.11.8"

version := "1.0-SNAPSHOT"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-Xlint", "-Ywarn-adapted-args", "-Xfatal-warnings", "-feature")

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

libraryDependencies ++= {
  Seq(
    // http://stackoverflow.com/a/34570734
    "org.scala-lang" % "scala-reflect" % "2.11.8"
    ,"org.scala-lang.modules" % "scala-xml_2.11" % "1.0.4"
    
    ,"com.typesafe.akka" %% "akka-http-core" % "2.4.10"
    ,"com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.10"
    ,"com.typesafe.akka" %% "akka-http-testkit" % "2.4.3"
    ,"org.scalatest" %% "scalatest" % "2.2.6"
  )
}

enablePlugins(JavaAppPackaging)

enablePlugins(DockerPlugin)

maintainer in Docker := "akka http playground"

packageSummary in Docker := "akka http example"

packageDescription := "akka http example"

packageName in Docker := "akka-http-example"

version in Docker := "red"

dockerBaseImage := "abigail/scala"

dockerExposedPorts := Seq(8080)

dockerRepository := Some("lotharschulz")


pomExtra :=
  <scm>
    <url>https://github.com/lotharschulz/akkahttp-playground</url>
    <connection>scm:git:github.com/lotharschulz/akkahttp-playground.git</connection>
    <developerConnection>scm:git:https://github.com/lotharschulz/akkahttp-playground.git</developerConnection>
  </scm>
    <developers>
      <developer>
        <id>lotharschulz</id>
        <name>lothar</name>
        <email>mail[at]lothar[minus]schulz[dot]info</email>
        <url>https://www.github.com/lotharschulz/</url>
      </developer>
    </developers>