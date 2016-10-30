name := """tizarah"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  filters,
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2",
  "com.h2database" % "h2" % "1.4.190",
  "mysql"               % "mysql-connector-java" % "5.1.39",
  "org.scalaz" %% "scalaz-core" % "7.2.6",

  // WebJars (i.e. client-side) dependencies
  "org.webjars" %% "webjars-play" % "2.5.0",
  "org.webjars" % "jquery" % "1.11.3",
  "org.webjars" % "bootstrap" % "3.3.6" exclude("org.webjars", "jquery"),
  "org.webjars" % "angularjs" % "1.5.5" exclude("org.webjars", "jquery"),
  "org.webjars" % "angular-ui-bootstrap" % "1.3.2" exclude("org.webjars", "jquery"),
  specs2 % Test
)

