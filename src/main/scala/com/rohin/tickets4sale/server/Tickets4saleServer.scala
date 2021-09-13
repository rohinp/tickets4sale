package com.rohin.tickets4sale.server

import com.rohin.tickets4sale.routes._
import com.rohin.tickets4sale.services._
import com.rohin.tickets4sale.db._
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
import com.typesafe.config.Config
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import scala.util.chaining._
import scala.concurrent.ExecutionContext.Implicits.global

import InitScript._

object Tickets4saleServer:

  def stream[F[_]: Async]: Stream[F, Nothing] = {
    given Config = ConfigFactory.load()
    given MongoDatabase = initDB

    val httpApp = (
      Tickets4saleRoutes.indexRoutes(Index.impl[F]) <+>
        Tickets4saleRoutes.inventoryRoutes(InventoryService.impl[F](new Tickets4SaleMongo)) <+>
        Tickets4saleRoutes.fileLoaderRoutes(TicketsUpload.impl(new Tickets4SaleMongo))
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
