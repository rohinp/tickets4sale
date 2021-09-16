package com.rohin.tickets4sale.db

import cats.Applicative
import cats.effect.IO
import cats.implicits._
import com.mongodb.client._
import com.mongodb.client.model._
import com.rohin.tickets4sale.core.domain._
import com.rohin.tickets4sale.core.errors.SimpleError._
import com.typesafe.config.Config
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import org.bson.Document
import org.bson.conversions.Bson
import org.log4s.getLogger

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.UUID
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.jdk.javaapi.CollectionConverters._
import scala.util.chaining._

import Performace.given
import Performace._

trait Tickets4SaleRepo[F[_]]:
  def findPerformacesByTitle(title: String): F[List[Performace]]
  def findPerformacesByDate(date: LocalDate): F[List[Performace]]
  def insertPerformances(performaces: List[Performace]): F[Int]
  def updatePerformaces(fav: FavTitle): F[Int]

class Tickets4SaleMongo[F[_]:Applicative](using c: Config, mongodb: MongoDatabase)
    extends Tickets4SaleRepo[F]:
  def find(query: Document): F[List[Performace]] =
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
        .pipe(loop).pure[F]

  override def findPerformacesByDate(date: LocalDate): F[List[Performace]] =
    find(new Document("showDate", date.toString))

  override def findPerformacesByTitle(title: String): F[List[Performace]] =
    find(new Document("title", title))

  override def insertPerformances(performaces: List[Performace]): F[Int] =
    val javaList: java.util.List[
      com.mongodb.client.model.InsertOneModel[org.bson.Document]
    ] = performaces
      .map(p => new InsertOneModel(Document.parse(p.asJson.noSpaces)))
      .asJava
    mongodb
        .getCollection(c.getString("tickets4Sale.collection.performances"))
        .bulkWrite(javaList)
        .getInsertedCount.pure[F]
     <* 
      getLogger.info(
        s"Records inserted ${performaces.length} with title ${performaces.headOption
          .map(d => (d.title, d.genre))
          .mkString}").pure[F]

  override def updatePerformaces(fav: FavTitle): F[Int] =
      mongodb
        .getCollection(c.getString("tickets4Sale.collection.performances"))
        .updateMany(
          new Document("title", fav.title),
          Updates.set("favrouite", fav.isFav)
        )
        .getModifiedCount
        .toInt.pure[F]
     <*
      getLogger.info(
        s"Updating ${fav.title} with fav value ${fav.isFav}"
      ).pure[F]
    

end Tickets4SaleMongo
