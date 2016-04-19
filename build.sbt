import sbt._

EclipseKeys.withSource := true

addCommandAlias("idea", "update-classifiers; update-sbt-classifiers; gen-idea sbt-classifiers")

libraryDependencies ++= Seq(
  "com.github.scopt" %% "scopt" % "3.3.0",
  "com.brindescu" %% "gumtree-facade" % "0.3" withSources() withJavadoc()
)

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

resolvers += Resolver.sonatypeRepo("public")

val mc = Some("edu.oregonstate.merging.operation.Main")

mainClass in (Compile, run) := mc

mainClass in assembly := mc

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
