package com.rohin.tickets4sale.core.domain


import scala.util.chaining._
import cats.instances.uuid
import java.util.UUID
import io.circe.Decoder
import io.circe.HCursor
import io.circe.Encoder
import io.circe.syntax._
import scala.util.Try
import java.text.SimpleDateFormat
import java.text.DateFormat
import java.time.LocalDate
import cats.syntax.apply._
enum Genre:
  case MUSICAL
  case COMEDY
  case DRAMA

case class Price(genre:Genre, price:BigDecimal)

trait DateEncDeco:
  given Decoder[LocalDate] = Decoder.decodeString.emapTry ( str => Try(LocalDate.parse(str)))
  given Encoder[LocalDate] = Encoder.instance(_.toString.asJson)

//Making Performace an Enum would have helped to encode success and falure encoding of 
//Performace esier.
case class RawPerformace(
  genre:Genre,
  title:String,
  showDate:LocalDate
)

object RawPerformace extends DateEncDeco:
  given Decoder[Genre] = Decoder.decodeString.emapTry(str => Try(Genre.valueOf(str)))

  given Decoder[RawPerformace] = 
    Decoder.forProduct3("genre", "title", "show_date")(RawPerformace.apply)

case class Performace(
  _id:UUID,
  genre:Genre,
  title:String,
  ticketsLeft:Int,
  ticketsAvailable:Int,
  showDate:LocalDate,
  favrouite:Boolean = false  
)

object Performace extends DateEncDeco:

  def apply(rp:RawPerformace, tickets:Int):Performace =
    Performace(_id = UUID.randomUUID, genre = rp.genre, title = rp.title, ticketsLeft = tickets, ticketsAvailable = tickets, showDate = rp.showDate)

  def dateRange(start: LocalDate, days:Int):LazyList[LocalDate] =
    val end = start.plusDays(days)
    LazyList.unfold(start)(c => if c.compareTo(end) == 0 then None else Some((c,c.plusDays(1))))

  def generatePerformaces(numOfPerformances:Int = 100):RawPerformace => List[Performace] = 
    p => 
      dateRange(p.showDate, numOfPerformances)
        .map(d => p.copy(showDate = d))
        .zipWithIndex
        .map((rp, index) => if index < 60 then Performace(rp,200) else Performace(rp,100))
        .toList
          
end Performace