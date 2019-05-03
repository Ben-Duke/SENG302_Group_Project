name := "team-800"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.8"

maintainer := "s302team800@cosc.canterbury.ac.nz"


lazy val myProject = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += "com.h2database" % "h2" % "1.4.197"
libraryDependencies += "org.glassfish.jaxb" % "jaxb-core" % "2.3.0.1"
libraryDependencies += "org.glassfish.jaxb" % "jaxb-runtime" % "2.3.2"

libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
// https://mvnrepository.com/artifact/org.json/json
libraryDependencies += "org.json" % "json" % "20180813"
libraryDependencies ++= Seq (
  "io.cucumber" % "cucumber-core" % "4.2.0" % " test ",
  "io.cucumber" % "cucumber-jvm" % "4.2.0" % " test ",
  "io.cucumber" % "cucumber-junit" % "4.2.0" % " test ",
  "io.cucumber" % "cucumber-java" % "4.2.0",
  "org.mockito" % "mockito-core" % "2.25.1" % " test "
)
testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8","-Xlint:unchecked", "-Xlint:deprecation")

scalacOptions := Seq("-target:jvm-1.8")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")

    sys.error(sys.props("java.specification.version") + " Java 8 is required for this project.")
}

configs(IntegrationTest)
Defaults.itSettings