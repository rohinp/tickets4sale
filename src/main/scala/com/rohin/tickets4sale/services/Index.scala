package com.rohin.tickets4sale.services

import cats.Applicative
import cats.implicits.*

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
          |     <meta charset="UTF-8">
          |     <meta name="viewport" content="width=device-width, initial-scale=1.0">
          |     <meta http-equiv="X-UA-Compatible" content="ie=edge">
          |     <link rel="stylesheet" href="https://unpkg.com/papercss@1.8.2/dist/paper.min.css">
          |     <title>Tickets4sale</title>  
          |    </head>
          |    <body>
          |        <div id="root"></div>
          |        <script>
          |         ${resource.getLines().mkString("\n")}
          |        </script>
          |    </body>
          |</html>
          |""".stripMargin

      }}.fold(_ => "<h1>Run command `npm run-script build` and then do `sbt runAPP`</h1>", d => d).pure[F]
  }
end Index
