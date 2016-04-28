import sbt._

EclipseKeys.withSource := true

addCommandAlias("idea", "update-classifiers; update-sbt-classifiers; gen-idea sbt-classifiers")

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.3.0",
  "com.brindescu" %% "gumtree-facade" % "0.3" withSources() withJavadoc()
)

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

resolvers += Resolver.sonatypeRepo("public")

resolvers += "Mine" at "http://releases.ivy.brindescu.com"

val mc = Some("edu.oregonstate.merging.operation.Main")

mainClass in (Compile, run) := mc

mainClass in assembly := mc

crossScalaVersions := Seq("2.10.6", "2.11.7")

lazy val ConflictDetector = (project in file(".")).
  settings(
    organization := "com.brindescu",
    name := "conflict-detector",
    version := "0.1"
  )

lazy val versionReport = TaskKey[String]("version-report")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

publishTo := {
  val prefix = if (isSnapshot.value) "snapshots" else "releases"
  Some("Webpage" at "s3://"+prefix+".ivy.brindescu.com")
}
