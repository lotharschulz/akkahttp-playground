import com.typesafe.sbt.SbtNativePackager.autoImport._
import sbt.Keys._

libraryDependencies ++= {
  Seq(
    // http://stackoverflow.com/a/34570734
     "org.scala-lang"         % "scala-reflect"  % "2.11.8"
    ,"org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5"

    ,"com.typesafe.akka" %% "akka-http-core"                    % "2.4.10"
    ,"com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.10"
    ,"com.typesafe.akka" %% "akka-http-testkit"                 % "2.4.3"
    ,"org.scalatest"     %% "scalatest"                         % "3.0.0"
  )
}

// sbt -DdockerOrganization=                  -DdockerName=                    -DdockerBImage=                            -DdockerRepo=                [sbt command]
// sbt -DdockerOrganization=info.lotharschulz -DdockerName=akkahttp-playground -DdockerBImage=lotharschulz/scala:0.0.2    -DdockerRepo=lotharschulz    [sbt command]
// sbt -DdockerOrganization=info.lotharschulz -DdockerName=akkahttp-playground -DdockerBImage=localhost:5000/scala:0.0.2  -DdockerRepo=localhost:5000  [sbt command]
lazy val dockerOrg    = sys.props.getOrElse("dockerOrganization",  default = "info.lotharschulz")
lazy val dockerName   = sys.props.getOrElse("dockerName",          default = "akkahttp-playground")
lazy val dockerBImage = sys.props.getOrElse("dockerBImage",        default = "localhost:5000/scala:0.0.2")
lazy val dockerRepo   = sys.props.getOrElse("dockerRepo",          default = "localhost:5000/scala:0.0.2")

// http://stackoverflow.com/questions/34404558/intellij-idea-and-sbt-syntax-error/35232279#35232279
lazy val root = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  enablePlugins(DockerPlugin).
  settings(
    organization  := dockerOrg,
    name          := dockerName,
    scalaVersion  := "2.11.8",
    version       := "0.0.1",
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-Xlint", "-Ywarn-adapted-args", "-Xfatal-warnings", "-feature"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),

    maintainer in Docker     := "Lothar Schulz <mail@lothar-schulz.info>",
    packageSummary in Docker := "akka http example",
    packageDescription       := "akka http example",
    packageName in Docker    := "akka-http-example",
    version in Docker        := "0.0.1",
    //dockerBaseImage        := "lotharschulz/scala:0.0.2",
    //dockerBaseImage        := "localhost:5000/scala:0.0.2",
    dockerBaseImage          := dockerBImage,
    dockerExposedPorts       := Seq(8181),
    //dockerRepository       := Some("lotharschulz"),
    //dockerRepository       := Some("localhost:5000"),
    dockerRepository         := Some(dockerRepo),
    dockerUpdateLatest       := false
  )


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
        <url>https://github.com/lotharschulz/</url>
      </developer>
    </developers>
