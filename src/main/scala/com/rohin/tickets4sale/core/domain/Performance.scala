package com.rohin.tickets4sale.core.domain

enum Genre:
  case Musicals
  case Comedies
  case Dramas

case class Price(genre:Genre, price:BigDecimal)

enum Status:
  case SaleNotStarted
  case OpenForSale
  case SoldOut
  case InThePast

case class Show(
  title:String,
  ticketsLeft:Int,
  ticketsAvailable:Int,
  status:Status
)

case class Performace(
  genre:Genre,
  shows:List[Status]
)