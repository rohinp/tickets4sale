package com.rohin.tickets4sale.routes

import com.rohin.tickets4sale.resources._
import org.http4s.headers.`Content-Type`
import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.MediaType

object Tickets4saleRoutes {

  def indexRoutes[F[_]: Sync](I: Index[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root =>
        for {
          index <- I.index
          resp <- Ok(index, `Content-Type`(MediaType.text.html))
        } yield resp
    }
  }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }
}
