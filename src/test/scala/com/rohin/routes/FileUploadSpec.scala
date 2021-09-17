package com.rohin.routes

import cats.effect.IO
import com.rohin.tickets4sale.routes._
import org.http4s.*
import org.http4s.implicits.*
import munit.CatsEffectSuite
import com.rohin.tickets4sale.services._
import org.http4s.syntax.header
import org.http4s.client.dsl.Http4sClientDsl
import java.net.URL
import org.http4s.multipart.Multipart
import org.http4s.multipart.Part
import org.http4s.headers._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.mongodb.client.MongoDatabase
import com.rohin.tickets4sale.server.InitScript
import com.rohin.tickets4sale.db.Tickets4SaleMongo
import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
class FileUpoadSpec extends CatsEffectSuite with Http4sClientDsl[IO] {
  given c:Config = ConfigFactory.load()
  given md:MongoDatabase = InitScript.initDB
  given Tickets4SaleMongo[IO] = new Tickets4SaleMongo[IO]

  test("File Upload returns status code 200 and data is uploaded in Mongo") {
    for 
      _ <- IO(md.getCollection("performances").drop())
      _ <- assertIO(fileUpload.map(_.status) ,Status.Ok)
      _ <- IO.sleep(5.seconds)
      _ <- IO(InitScript.embededMongo.stop())
    yield ()
    
  }

  private[this] val fileUpload: IO[Response[IO]] = {
    import cats.effect.IO.asyncForIO
    
    val csv: URL = getClass.getResource("/shows-21_22.csv")

    val multipart = Multipart[IO](
      Vector(
        Part.fileData("dataFile", csv, `Content-Type`(MediaType.text.csv))
      ))

    val putfileUpoad = Request[IO](
      method = Method.PUT, 
      uri = uri"/inventory",
      headers = multipart.headers,
      body = EntityEncoder[IO, Multipart[IO]].toEntity(multipart).body
    )
    val tu = TicketsUpload.impl
    Tickets4saleRoutes.fileLoaderRoutes(tu).orNotFound(putfileUpoad)
  }

}