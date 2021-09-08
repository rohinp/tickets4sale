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
    scalaVersion := "3.0.1",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
      "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.scalameta"   %% "munit"               % MunitVersion           % Test,
      "org.typelevel"   %% "munit-cats-effect-3" % MunitCatsEffectVersion % Test,
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )

import scala.sys.process._
lazy val initScript = taskKey[Unit]("Execute the init script.")

initScript :=
  Using.Manager(op => {
    op(scala.io.Source.fromFile("./output/bundle.js"))
  }).fold(_ => {
    "npm run-script build" !
  }, _ => ())

addCommandAlias("runApp", ";initScript;run")
