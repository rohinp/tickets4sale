package com.rohin.tickets4sale.core.domain

import cats.instances.uuid
import cats.syntax.apply._
import io.circe.Decoder
import io.circe.Encoder
import io.circe.HCursor
import io.circe.syntax._

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.UUID
import scala.util.Try
import scala.util.chaining._

enum Genre:
  case MUSICAL
  case COMEDY
  case DRAMA

case class FavTitle(title: String, isFav: Boolean)

trait DateEncDeco:
  given Decoder[LocalDate] =
    Decoder.decodeString.emapTry(str => Try(LocalDate.parse(str)))
  given Encoder[LocalDate] = Encoder.instance(_.toString.asJson)

//Making Performace an Enum would have helped to encode success and falure encoding of
//Performace esier.
case class RawPerformace(
    genre: Genre,
    title: String,
    showDate: LocalDate
)

object RawPerformace extends DateEncDeco:
  given Decoder[Genre] =
    Decoder.decodeString.emapTry(str => Try(Genre.valueOf(str)))
  given Encoder[Genre] = Encoder.instance(_.toString.asJson)

  given Decoder[RawPerformace] =
    Decoder.forProduct3("genre", "title", "showDate")(RawPerformace.apply)

enum Hall(val hallSize: Int, val maxPurchase: Int):
  case Small extends Hall(100, 5)
  case Big extends Hall(200, 10)

case class Performace(
    _id: UUID,
    genre: Genre,
    title: String,
    hallSize: Int,
    maxPurchase:Int,
    ticketsLeft: Int,
    ticketsSold:Int,
    showDate: LocalDate,
    favrouite: Boolean = false
)

object Performace extends DateEncDeco:

  def apply(rp: RawPerformace, hallSize: Int, maxPurchase:Int, ticketsSold:Int): Performace =
    Performace(
      _id = UUID.randomUUID,
      genre = rp.genre,
      title = rp.title,
      hallSize = hallSize,
      maxPurchase = maxPurchase,
      ticketsLeft = hallSize - ticketsSold,
      ticketsSold = ticketsSold,
      showDate = rp.showDate
    )

  def dateRange(start: LocalDate, days: Int): LazyList[LocalDate] =
    val end = start.plusDays(days)
    LazyList.unfold(start)(c =>
      if c.compareTo(end) == 0 then None else Some((c, c.plusDays(1)))
    )

  def generatePerformaces(
      numOfPerformances: Int = 100
  ): RawPerformace => List[Performace] =
    p =>
      dateRange(p.showDate, numOfPerformances)
        .map(d => p.copy(showDate = d))
        .zipWithIndex
        .map((rp, index) =>
          if index < 60 then Performace(rp, Hall.Big.hallSize, Hall.Big.maxPurchase, 0)
          else Performace(rp, Hall.Small.hallSize, Hall.Small.maxPurchase, 0)
        )
        .toList

end Performace
