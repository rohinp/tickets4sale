package com.rohin.tickets4sale.db

import com.rohin.tickets4sale.core.domain.Performace
import java.util.UUID
import com.mongodb.client.MongoDatabase
import scala.concurrent.Future
import scala.util.chaining._
import com.typesafe.config.Config
import scala.concurrent.ExecutionContext
import org.bson.conversions.Bson
import org.bson.Document
import java.text.SimpleDateFormat
import scala.jdk.javaapi.CollectionConverters._
import scala.collection.JavaConverters._
import com.mongodb.client.MongoCursor
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser.decode
import Performace.given
import Performace._
import com.rohin.tickets4sale.core.errors.SimpleError._
import com.mongodb.client.model.InsertOneModel
import com.mongodb.client.model.ReplaceOneModel
import java.time.LocalDate
import cats.effect.IO
import org.log4s.getLogger
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateManyModel
import com.mongodb.client.model.UpdateOptions
import com.rohin.tickets4sale.core.domain.FavTitle
import com.mongodb.client.model.Updates

trait Tickets4SaleRepo[F[_]]:
  def findPerformacesByTitle(title: String): F[List[Performace]]
  def findPerformacesByDate(date: LocalDate): F[List[Performace]]
  def insertPerformances(performaces: List[Performace]): F[Int]
  def updatePerformaces(fav: FavTitle): F[Int]

class Tickets4SaleMongo(using c: Config, mongodb: MongoDatabase)
    extends Tickets4SaleRepo[IO]:
  def find(query: Document): IO[List[Performace]] =
    IO {
      def loop(cursor: MongoCursor[Document]): List[Performace] =
        if cursor.hasNext then
          decode[Performace](cursor.next.toJson)
            .fold(
              e => throw PerformanceDecodeFailure(e.getMessage),
              v => v :: loop(cursor)
            )
        else List()

      mongodb
        .getCollection(c.getString("tickets4Sale.collection.performances"))
        .find(query)
        .iterator
        .pipe(loop)
    }

  override def findPerformacesByDate(date: LocalDate): IO[List[Performace]] =
    find(new Document("showDate", date.toString))

  override def findPerformacesByTitle(title: String): IO[List[Performace]] =
    find(new Document("title", title))

  override def insertPerformances(performaces: List[Performace]): IO[Int] =
    val javaList: java.util.List[
      com.mongodb.client.model.InsertOneModel[org.bson.Document]
    ] = performaces
      .map(p => new InsertOneModel(Document.parse(p.asJson.noSpaces)))
      .asJava
    IO(
      mongodb
        .getCollection(c.getString("tickets4Sale.collection.performances"))
        .bulkWrite(javaList)
        .getInsertedCount
    ) <* IO(
      getLogger.info(
        s"Records inserted ${performaces.length} with title ${performaces.headOption
          .map(d => (d.title, d.genre))
          .mkString}"
      )
    )

  override def updatePerformaces(fav: FavTitle): IO[Int] =
    IO(
      mongodb
        .getCollection(c.getString("tickets4Sale.collection.performances"))
        .updateMany(
          new Document("title", fav.title),
          Updates.set("favrouite", fav.isFav)
        )
        .getModifiedCount
        .toInt
    ) <* IO(
      getLogger.info(
        s"Updating ${fav.title} with fav value ${fav.isFav}"
      )
    )

end Tickets4SaleMongo
