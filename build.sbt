name := "team-800"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.8"

maintainer := "s302team800@cosc.canterbury.ac.nz"


lazy val myProject = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += javaJdbc % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.197"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.41"


libraryDependencies += evolutions

libraryDependencies += "org.glassfish.jaxb" % "jaxb-core" % "2.3.0.1"
libraryDependencies += "org.glassfish.jaxb" % "jaxb-runtime" % "2.3.2"

libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test

libraryDependencies ++= Seq (
  "io.cucumber" % "cucumber-core" % "4.2.0" % " test ",
  "io.cucumber" % "cucumber-jvm" % "4.2.0" % " test ",
  "io.cucumber" % "cucumber-junit" % "4.2.0" % " test ",
  "io.cucumber" % "cucumber-java" % "4.2.0",
  "org.mockito" % "mockito-core" % "2.25.1" % " test "
)

javaOptions in Test ++= Seq("-Dconfig.file=conf/test.conf")

testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")


javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
configs(IntegrationTest)
Defaults.itSettings

libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"