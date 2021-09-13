package com.rohin.tickets4sale.services

import com.rohin.tickets4sale.db._
import org.apache.commons.csv.CSVRecord
import com.rohin.tickets4sale.core.domain._
import cats.Applicative
import scala.util.chaining._
import collection.JavaConverters._
import org.apache.commons.csv.CSVParser
import cats.implicits._
import cats.Monad
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser.decode
import com.rohin.tickets4sale.core.errors.SimpleError._
import cats.effect.IO

trait TicketsUpload[F[_]]:
  type Performances = List[Performace]
  def csvRecordToPerformance:CSVParser => F[List[RawPerformace]]
  def generatePerformanceRecords:List[RawPerformace] => F[List[Performances]]
  def storePerformance:List[Performances] => F[Int]
  def bulkUpload:CSVParser => F[Int]

object TicketsUpload:
  import RawPerformace.given

  def impl(ticketsRepo:Tickets4SaleRepo[IO]): TicketsUpload[IO] = new TicketsUpload[IO]{
    
    override def csvRecordToPerformance:CSVParser => IO[List[RawPerformace]] = 
      parser => 
        IO(parser.iterator.asScala.toList
        .filter(r => r.get(0).nonEmpty && r.get(1).nonEmpty && r.get(2).nonEmpty)
        .map(r => decode[RawPerformace](s"""{"genre": "${r.get(2)}", "title": "${r.get(0)}", "start_date": "${r.get(1)}"}"""))
        .pipe(ls => if ls.exists(_.isLeft) then throw RawPerformanceDecodeFailure(ls.filter(_.isLeft).mkString("\n")) else ls)
        .map(_.toOption.get))

    override def generatePerformanceRecords:List[RawPerformace] => IO[List[Performances]] = 
      rps =>
        IO(
          for 
            rp <- rps
          yield Performace.generatePerformaces(100)(rp))
        
    override def storePerformance:List[Performances] => IO[Int] = 
      pfs =>
        pfs.parTraverse(ticketsRepo.insertPerformances)
          .map(_.sum)

    override def bulkUpload:CSVParser => IO[Int] =
      p => csvRecordToPerformance(p) flatMap generatePerformanceRecords flatMap storePerformance
  }
end TicketsUpload