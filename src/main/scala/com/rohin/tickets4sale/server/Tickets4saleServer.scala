package com.rohin.tickets4sale.server

import cats.effect.Async
import cats.effect.Resource
import cats.syntax.all._
import com.comcast.ip4s._
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.rohin.tickets4sale.db._
import com.rohin.tickets4sale.routes._
import com.rohin.tickets4sale.services._
import com.typesafe.config._
import de.svenkubiak.embeddedmongodb.EmbeddedMongoDB
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import Tickets4saleRoutes._
import scala.util.chaining._

import InitScript._

object Tickets4saleServer:

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    given Config = ConfigFactory.load()
    given MongoDatabase = initDB
    given Tickets4SaleMongo[F] = new Tickets4SaleMongo[F]

    val httpApp = (
      indexRoutes(Index.impl[F]) <+>
      inventoryRoutes(InventoryService.impl[F]) <+>
      fileLoaderRoutes(TicketsUpload.impl[F]) <+>
      markFavrouiteRoutes(MakeFavService.impl[F])
    ).orNotFound

    // With Middlewares in place
    val finalHttpApp = Logger.httpApp(true, false)(httpApp)
    for
      exitCode <- Stream.resource(
        EmberServerBuilder
          .default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[F].never)
      )
    yield exitCode
  }.drain

end Tickets4saleServer
