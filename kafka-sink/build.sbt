name := "kafka-sink"

version := "0.1"

organization := "io.trosa"

description := "IRC TCP connection to Kafka MQ Graph."

scalaVersion := "2.12.7"

/// Dependencies ///

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.17",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.17" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.17",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.17" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.5",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test
)

libraryDependencies += "org.typelevel" %% "cats-core" % "1.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "1.0-M1"