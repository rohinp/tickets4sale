package com.rohin.tickets4sale

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.rohin.tickets4sale.server.Tickets4saleServer

object Main extends IOApp {
  def run(args: List[String]) =
    Tickets4saleServer.stream[IO].compile.drain.as(ExitCode.Success)
}
