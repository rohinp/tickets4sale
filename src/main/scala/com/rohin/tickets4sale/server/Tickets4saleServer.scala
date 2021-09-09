package com.rohin.tickets4sale.server

import com.rohin.tickets4sale.routes._
import com.rohin.tickets4sale.resources._
import cats.effect.{Async, Resource}
import cats.syntax.all._
import com.comcast.ip4s._
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import de.svenkubiak.embeddedmongodb.EmbeddedMongoDB
import com.typesafe.config.ConfigFactory
import com.mongodb.MongoClient
import scala.util.chaining._

import InitScript._

object Tickets4saleServer:

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    lazy val conf = ConfigFactory.load();
    val mongoDB = initializeDB(conf)

    val httpApp = (
      Tickets4saleRoutes.indexRoutes(Index.impl[F]) <+>
        Tickets4saleRoutes.helloWorldRoutes(HelloWorld.impl[F])
      ).orNotFound

    // With Middlewares in place
    val finalHttpApp = Logger.httpApp(true, false)(httpApp)
    for
      exitCode <- Stream.resource(
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build >>
        Resource.eval(Async[F].never)
      )
    yield exitCode
  }.drain

end Tickets4saleServer
