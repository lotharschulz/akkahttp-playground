//logLevel := Level.Debug
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
// resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
//addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")