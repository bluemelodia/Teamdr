name := """Teamdr1"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.mockito" % "mockito-all" % "1.9.5",
  "org.powermock" % "powermock-api-mockito" % "1.5.1",
  "org.powermock" % "powermock-module-junit4" % "1.6.2"
)

// Allows us to use Java
libraryDependencies += javaCore

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
