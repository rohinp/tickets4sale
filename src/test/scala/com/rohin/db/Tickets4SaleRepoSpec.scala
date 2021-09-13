package com.rohin.db

import cats.effect.IO
import com.rohin.tickets4sale.routes._
import org.http4s.*
import org.http4s.implicits.*
import munit.CatsEffectSuite
import com.rohin.tickets4sale.services._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.mongodb.client.MongoDatabase
import com.rohin.tickets4sale.server.InitScript
import com.rohin.tickets4sale.db.Tickets4SaleMongo
import java.io.FileReader
import org.apache.commons.csv.CSVFormat

class Tickets4SaleRepoSpec extends CatsEffectSuite {
  given c:Config = ConfigFactory.load()
  given md:MongoDatabase = InitScript.initDB

  test("Insert records in mongo from file") {
    assertIO(fileUpload ,300)
  }

  private[this] val fileUpload: IO[Int] = {
    val csv = getClass().getResource("/small.csv")
    val salesMongo = new Tickets4SaleMongo
    TicketsUpload.impl(salesMongo).bulkUpload(CSVFormat.EXCEL.parse(new FileReader(csv.getPath)))
  }

}