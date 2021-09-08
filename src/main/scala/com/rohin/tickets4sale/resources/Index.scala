package com.rohin.tickets4sale.resources

import cats.Applicative
import cats.implicits.*

import scala.util.Using
import scala.util.Using.*

trait Index[F[_]]:
  def index:F[String]

object Index:
  def impl[F[_]: Applicative]: Index[F] = new Index[F]{
    def index: F[String] =
      Manager{ op => {
        val resource = op(scala.io.Source.fromFile("./output/bundle.js"))
        s"""
          |<!DOCTYPE html>
          |<html>
          |    <head>
          |        <meta charset="utf-8" />
          |    </head>
          |    <body>
          |        <div id="root"></div>
          |        <script>
          |         ${resource.getLines().mkString("\n")}
          |        </script>
          |    </body>
          |</html>
          |""".stripMargin

      }}.fold(_ => "<h1>Run command `npm run-script build` and then try again</h1>", d => d).pure[F]
  }
end Index
