package com.rohin.tickets4sale.core.domain

import cats.kernel.Semigroup

import java.time.LocalDate
import io.circe.Decoder.state

enum Status(value:String):
  case SaleNotStarted extends Status("sale not stated")
  case OpenForSale extends Status("open for sale")
  case SoldOut extends Status("sold out")
  case InThePast  extends Status("in the past")

  def stringify:String =
    this match
      case SaleNotStarted => "sale not started"
      case OpenForSale => "open for sale"
      case SoldOut => "sold out"
      case InThePast  => "in the past"

case class Show(
  title:String,
  ticketsLeft:Int,
  ticketsAvailable:Int,
  status:String,
  favrouite:Boolean
)

case class SingleEntry(
  genre:String,
  price:Int,
  shows:List[Show]
)

case class Inventory(
  inventory:List[SingleEntry]
)

object Inventory:
  //Assume All performace are of same date,
  //Improvement: We could have added an extra validation in this case for date
  def toInventory(queryDate:LocalDate):List[Performace] => Inventory = 
    performaces => Inventory(performaces
                    .groupBy(_.genre)
                    .map((genre, ps) => SingleEntry(
                          genre = genre.toString,
                          price = defaultPrices(genre),
                          shows = ps.map(p => 
                            val status = calculateStatus(p,queryDate)
                            Show(
                              p.title,
                              p.ticketsLeft, 
                              calculateTicketsAvailable(p)(status), 
                              status.stringify,
                              p.favrouite))
                        )).toList)  

  def calculateStatus(p:Performace, queryDate:LocalDate):Status =
    if queryDate.equals(p.showDate) || queryDate.isBefore(p.showDate) then 
      if p.ticketsLeft <= 0 || queryDate.isAfter(p.showDate.minusDays(6)) then
        Status.SoldOut
      else
        p.showDate.isBefore(queryDate.plusDays(25)) match
          case true => Status.OpenForSale 
          case false => Status.SaleNotStarted
    else 
      Status.InThePast
      
  def calculateTicketsAvailable:Performace => Status => Int =
    p => status => status match
      case Status.OpenForSale => p.maxPurchase
      case _ => 0

  def defaultPrices:Map[Genre, Int] =
    Map(
      Genre.MUSICAL -> 70,
      Genre.COMEDY -> 50,
      Genre.DRAMA -> 40
    )
end Inventory