package com.rohin.tickets4sale

import cats.effect.IO
import com.rohin.tickets4sale.routes.Tickets4saleRoutes
import org.http4s.*
import org.http4s.implicits.*
import munit.CatsEffectSuite
import com.rohin.tickets4sale.services._

class HelloWorldSpec extends CatsEffectSuite {

  test("HelloWorld returns status code 200") {
    assertIO(retHelloWorld.map(_.status) ,Status.Ok)
  }

  test("HelloWorld returns hello world message") {
    assertIO(retHelloWorld.flatMap(_.as[String]), "{\"message\":\"Hello, world\"}")
  }

  private[this] val retHelloWorld: IO[Response[IO]] = {
    val getHW = Request[IO](Method.GET, uri"/hello/world")
    val helloWorld = InventoryService.impl[IO]
    Tickets4saleRoutes.inventoryRoutes(helloWorld).orNotFound(getHW)
  }
}