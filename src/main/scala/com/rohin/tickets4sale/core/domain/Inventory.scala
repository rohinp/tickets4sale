package com.rohin.tickets4sale.core.domain

import java.time.LocalDate
import cats.kernel.Semigroup

enum Status(value:String):
  case SaleNotStarted extends Status("sale not stated")
  case OpenForSale extends Status("open for sale")
  case SoldOut extends Status("sold out")
  case InThePast  extends Status("in the past")

  def stringify:String =
    this match
      case SaleNotStarted => "sale not stated"
      case OpenForSale => "open for sale"
      case SoldOut => "sold out"
      case InThePast  => "in the past"

case class Show(
  title:String,
  ticketsLeft:Int,
  ticketsAvailable:Int,
  status:String
)

case class SingleEntry(
  genre:Genre,
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
                    .map((genre, ps) => {
                        SingleEntry(
                          genre = genre,
                          shows = ps.map(p => {
                            Show(
                                title = p.title,
                                ticketsLeft = p.ticketsLeft,
                                ticketsAvailable = p.ticketsAvailable,
                                status = calculateStatus(p,queryDate).stringify
                            )
                          })
                        )
                    }).toList)  

  def calculateStatus(p:Performace, queryDate:LocalDate):Status =
    if queryDate.isBefore(p.showDate) then 
      if p.ticketsLeft > 0 then
        Status.SoldOut
      else
        queryDate.plusDays(20).isBefore(p.showDate) match
          case true => Status.OpenForSale 
          case false => Status.SaleNotStarted
    else 
      Status.InThePast

  given Semigroup[Inventory] = new Semigroup[Inventory]{
    def combine(x: Inventory, y: Inventory): Inventory = Inventory(x.inventory ++ y.inventory)
  }
end Inventory