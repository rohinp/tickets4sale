package com.rohin.tickets4sale.services

import cats._
import cats.implicits._
import com.rohin.tickets4sale.core.domain._
import com.rohin.tickets4sale.core.errors.SimpleError._
import com.rohin.tickets4sale.db._
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import org.apache.commons.csv._

import scala.util.chaining._

import collection.JavaConverters._
import java.time.LocalDate

trait TicketsUpload[F[_] :Monad]:
  type Performances = List[Performace]
  def csvRecordToPerformance:CSVParser => F[List[RawPerformace]]
  def generatePerformanceRecords:List[RawPerformace] => F[List[Performances]]
  def storePerformance:List[Performances] => F[Int]
  def filterAlreadyExists:List[RawPerformace] => F[List[RawPerformace]]
  def bulkUpload:CSVParser => F[Int] =
    p => csvRecordToPerformance(p) flatMap 
          filterAlreadyExists flatMap 
          generatePerformanceRecords flatMap 
          storePerformance

object TicketsUpload:
  import RawPerformace.given

  def impl[F[_]:Monad](using ticketsRepo:Tickets4SaleRepo[F]): TicketsUpload[F] = new TicketsUpload[F]{
    
    override def csvRecordToPerformance:CSVParser => F[List[RawPerformace]] = 
      parser => 
        parser.iterator.asScala.toList
        .filter(_.size == 3)
        .map(r => decode[RawPerformace](s"""{"genre": "${r.get(2)}", "title": "${r.get(0)}", "showDate": "${r.get(1)}"}"""))
        .pipe(ls => if ls.exists(_.isLeft) then throw RawPerformanceDecodeFailure(ls.filter(_.isLeft).mkString("\n")) else ls)
        .map(_.toOption.get).pure[F]

    override def generatePerformanceRecords:List[RawPerformace] => F[List[Performances]] = 
      rps =>
        (
          for 
            rp <- rps
          yield Performace.generatePerformaces(100)(rp)).pure[F]
          
    override def storePerformance:List[Performances] => F[Int] = 
      _.traverse(ticketsRepo.insertPerformances)
          .map(_.sum)

    override def filterAlreadyExists:List[RawPerformace] => F[List[RawPerformace]] =
      _.traverseFilter(rp => 
          ticketsRepo.findPerformacesByDate(rp.showDate)
            .map(ps => if ps.exists(_.title == rp.title) then None else Some(rp)))
          
  }
end TicketsUpload