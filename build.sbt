import com.typesafe.sbt.SbtNativePackager.autoImport._
import sbt.Keys._

lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.12.0"

libraryDependencies ++= {
  Seq(
    // http://stackoverflow.com/a/34570734
     "org.scala-lang"         % "scala-reflect"  % "2.11.8"
    ,"org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5"

    ,"com.typesafe.akka" %% "akka-http-core"                    % "2.4.11"
    ,"com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.11"
    ,"com.typesafe.akka" %% "akka-http-testkit"                 % "2.4.11"
    ,"org.scalatest"     %% "scalatest"                         % "3.0.1"
  )
}

/*
sbt -DdockerOrganization=info.lotharschulz  -DdockerName=akkahttp-playground -DdockerBImage=localhost:5000/scala:0.0.3  -DdockerRepo=localhost:5000                  -DartefactVersion=0.0.3  -DversionInDocker=0.0.3  -DdockerPackageName=akkahttp-playground [sbt command]
sbt -DdockerOrganization=info.lotharschulz  -DdockerName=akkahttp-playground -DdockerBImage=lotharschulz/scala:0.0.3    -DdockerRepo=lotharschulz                    -DartefactVersion=0.0.3  -DversionInDocker=0.0.3  -DdockerPackageName=akkahttp-playground [sbt command]
sbt -DdockerOrganization=info.lotharschulz  -DdockerName=akkahttp-playground -DdockerBImage=lotharschulz/scala:0.0.3    -DdockerRepo=pierone.stups.zalan.do/automata -DartefactVersion=0.0.3  -DversionInDocker=0.0.3  -DdockerPackageName=akkahttp-playground [sbt command]
sbt -DdockerOrganization=info.lotharschulz  -DdockerName=akkahttp-playground -DdockerBImage=lotharschulz/scala:0.0.3    -DdockerRepo=gcr.io                          -DartefactVersion=v0.0.3 -DversionInDocker=v0.0.3 -DdockerPackageName=akkahttp-playground-gproj/akkahttp-playground [sbt command]

sbt -DuberjarName=uberjar.jar assembly 
*/
lazy val dockerOrg          = sys.props.getOrElse("dockerOrganization",  default = "info.lotharschulz")
lazy val dockerName         = sys.props.getOrElse("dockerName",          default = "akkahttp-playground")
lazy val dockerBImage       = sys.props.getOrElse("dockerBImage",        default = "localhost:5000/scala:0.0.2")
lazy val dockerRepo         = sys.props.getOrElse("dockerRepo",          default = "localhost:5000/scala:0.0.2")
lazy val artefactVersion    = sys.props.getOrElse("artefactVersion",     default = "0.0.2")
lazy val versionInDocker    = sys.props.getOrElse("versionInDocker",     default = "0.0.2")
lazy val dockerPackageName  = sys.props.getOrElse("dockerPackageName",   default = "akkahttp-playground")
lazy val uberjarName        = sys.props.getOrElse("uberjarName",         default = "uberjar.jar")

// http://stackoverflow.com/questions/34404558/intellij-idea-and-sbt-syntax-error/35232279#35232279
lazy val root = (project in file(".")).
  enablePlugins(JavaAppPackaging).
  enablePlugins(DockerPlugin).
  settings(
    organization     := dockerOrg,
    name             := dockerName,
    scalaVersion     := "2.11.8",
    version          := artefactVersion,
    test in assembly := {},
    scalacOptions    := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-Xlint", "-Ywarn-adapted-args", "-Xfatal-warnings", "-feature"),
    javacOptions     := Seq("-Xlint:unchecked", "-Xlint:deprecation", "-source", "1.8", "-target", "1.8"),
    
    // http://www.scala-sbt.org/sbt-native-packager/archetypes/java_app/customize.html#via-build-sbt
    javaOptions in Universal ++= Seq(
      "-J-Xms1024m",
      "-J-Xmx2048m"
    ),

    maintainer in Docker     := "Lothar Schulz <mail@lothar-schulz.info>",
    packageSummary in Docker := "akka http example",
    packageDescription       := "akka http example",
    packageName in Docker    := dockerPackageName,
    version in Docker        := versionInDocker,
    dockerBaseImage          := dockerBImage,
    dockerExposedPorts       := Seq(8181),
    dockerRepository         := Some(dockerRepo),
    dockerUpdateLatest       := false,

    // http://www.scala-sbt.org/release/docs/Basic-Def-Examples.html
    libraryDependencies      += scalacheck % Test,
    maxErrors                := 20,
    pollInterval             := 1000,

    initialCommands          := """
                         |import System.{currentTimeMillis => now}
                         |def time[T](f: => T): T = {
                         |  val start = now
                         |  try { f } finally { println("Elapsed: " + (now - start)/1000.0 + " s") }
                         |}""".stripMargin,

    mainClass in (Compile, packageBin) := Some("info.lotharschulz.MyService"),
    mainClass in (Compile, run)        := Some("info.lotharschulz.MyService"),
    mainClass in (Compile, assembly)   := Some("info.lotharschulz.MyService"),

    assemblyJarName in assembly := uberjarName,
    
    ivyLoggingLevel          := UpdateLogging.Full,
    shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " },
    shellPrompt              := { state => System.getProperty("user.name") + "> " },
    showTiming               := true,
    showSuccess              := true,
    crossPaths               := true,
    fork                     := false,
    fork in Test             := true,

    logLevel in compile      := Level.Info, // Level.Warn
    logLevel                 := Level.Info, // Level.Warn
    persistLogLevel          := Level.Info, // Level.Warn

    traceLevel               := 10,
    traceLevel               := 0,
    retrieveManaged          := true,

    timingFormat             := {
      import java.text.DateFormat
      DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL)
    }
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
