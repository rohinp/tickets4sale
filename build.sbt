import sbt.BasicCommands.alias
import sbt.BuiltinCommands.eval

import scala.language.postfixOps
import scala.util.Using

val Http4sVersion = "1.0.0-M24"
val CirceVersion = "0.15.0-M1"
val MunitVersion = "0.7.27"
val LogbackVersion = "1.2.5"
val MunitCatsEffectVersion = "1.0.5"

lazy val root = (project in file("."))
  .settings(
    organization := "com.rohin",
    name := "tickets4sale",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.1.1",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-parser"       % CirceVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "de.svenkubiak"   %  "embedded-mongodb"    % "5.1.1",
      "org.mongodb"     % "mongo-java-driver"    %"3.12.10",
      "com.typesafe"    % "config"               % "1.4.1",
      "org.apache.commons" % "commons-csv"       % "1.9.0"
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )

scalacOptions ++= Seq(
  "-encoding", "UTF-8",   // source files are in UTF-8
  "-deprecation",         // warn about use of deprecated APIs
  "-unchecked",           // warn about unchecked type parameters
  "-feature",             // warn about misused language features
  "-language:higherKinds",// allow higher kinded types without `import scala.language.higherKinds`
  "-Yshow-suppressed-errors",
  "-language:implicitConversions"
)

import scala.sys.process._
lazy val initScript = taskKey[Unit]("Execute the init script.")
lazy val buildUI    = taskKey[Unit]("Execute the npm build")

initScript :=
  Using.Manager(op => {
    op(scala.io.Source.fromFile("./output/bundle.js"))
  }).fold(_ => {
    "npm run-script build" !
  }, _ => ())

buildUI := {
  "npm run-script build" !
}
addCommandAlias("runApp", ";initScript;run")
addCommandAlias("buildAndRun", ";buildUI;run")
