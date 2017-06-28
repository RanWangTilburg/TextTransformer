import com.typesafe.sbt.SbtAspectj._

name := """TextTranformer"""

version := "2.4.4"

scalaVersion := "2.11.2"

//val kamonVersion = "0.5.1"
//
//libraryDependencies ++= Seq(
//  "io.kamon" %% "kamon-core" % kamonVersion,
//  "io.kamon" %% "kamon-akka" % kamonVersion,
//  "io.kamon" %% "kamon-statsd" % kamonVersion,
//  "io.kamon" %% "kamon-log-reporter" % kamonVersion,
//  "io.kamon" %% "kamon-system-metrics" % kamonVersion,
//  "org.aspectj" % "aspectjweaver" % "1.8.5"
//)
mainClass in assembly := some("transformer.main")
assemblyJarName := "output.jar"

val meta = """META.INF(.)*""".r
assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case n if n.endsWith(".conf") => MergeStrategy.concat
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}

libraryDependencies ++= Seq(
"com.typesafe.akka" %% "akka-actor" % "2.4.4",
"com.typesafe.akka" %% "akka-agent" % "2.4.4",
"com.typesafe.akka" %% "akka-camel" % "2.4.4",
"com.typesafe.akka" %% "akka-cluster" % "2.4.4",
"com.typesafe.akka" %% "akka-cluster-metrics" % "2.4.4",
"com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.4",
"com.typesafe.akka" %% "akka-cluster-tools" % "2.4.4",
"com.typesafe.akka" %% "akka-contrib" % "2.4.4",
"com.typesafe.akka" %% "akka-http-core" % "2.4.4",
"com.typesafe.akka" %% "akka-http-testkit" % "2.4.4",
"com.typesafe.akka" %% "akka-multi-node-testkit" % "2.4.4",
"com.typesafe.akka" %% "akka-osgi" % "2.4.4",
"com.typesafe.akka" %% "akka-persistence" % "2.4.4",
"com.typesafe.akka" %% "akka-persistence-tck" % "2.4.4",
"com.typesafe.akka" %% "akka-remote" % "2.4.4",
"com.typesafe.akka" %% "akka-slf4j" % "2.4.4",
"com.typesafe.akka" %% "akka-stream" % "2.4.4",
"com.typesafe.akka" %% "akka-stream-testkit" % "2.4.4",
"com.typesafe.akka" %% "akka-testkit" % "2.4.4",
"com.typesafe.akka" %% "akka-distributed-data-experimental" % "2.4.4",
"com.typesafe.akka" %% "akka-typed-experimental" % "2.4.4",
"com.typesafe.akka" %% "akka-http-experimental" % "2.4.4",
"com.typesafe.akka" %% "akka-http-jackson-experimental" % "2.4.4",
"com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.4",
"com.typesafe.akka" %% "akka-http-xml-experimental" % "2.4.4",
"com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.4",
"com.typesafe.akka" %% "akka-typed-experimental" % "2.4.4",
"edu.stanford.nlp" % "stanford-corenlp" % "3.4",
"edu.stanford.nlp" % "stanford-corenlp" % "3.4" classifier "models",
"edu.stanford.nlp" % "stanford-parser" % "3.4",
  "org.languagetool" % "languagetool-core" % "3.2",
  "org.languagetool" % "language-en" % "3.2",
  "com.softwaremill.scalamacrodebug" %% "macros" % "0.4",
  "org.scalatest" % "scalatest_2.11" % "2.0" % "test"
)
dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
)

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.3"
libraryDependencies += "com.databricks" %% "spark-csv" % "1.2.0"


libraryDependencies ++= Seq(
  "com.github.nscala-time" %% "nscala-time" % "2.2.0",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "jline" % "jline" % "2.13"
)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
homepage := Some(url("http://github.com/a8m/pb-scala"))

publishMavenStyle := true
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
pomExtra := (
  <scm>
    <url>git@github.com:a8m/pb-scala.git</url>
    <connection>scm:git:git@github.com:a8m/pb-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>a8m</id>
      <name>Ariel Mashraki</name>
      <url>http://github.com/a8m/</url>
    </developer>
  </developers>
)

aspectjSettings

javaOptions <++= AspectjKeys.weaverOptions in Aspectj

fork in run := true
//Add a comment

