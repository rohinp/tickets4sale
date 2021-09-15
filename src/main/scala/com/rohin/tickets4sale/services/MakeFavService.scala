package com.rohin.tickets4sale.services

import cats.effect.IO
import com.rohin.tickets4sale.db.Tickets4SaleRepo
import com.rohin.tickets4sale.core.domain._

trait MakeFavService[F[_]]:
  def updateFav(fav:FavTitle):F[Int]

object MakeFavService:
  def impl(ticketsRepo:Tickets4SaleRepo[IO]): MakeFavService[IO] = new MakeFavService[IO]{
    def updateFav(fav:FavTitle):IO[Int] = 
      ticketsRepo.updatePerformaces(fav)
  }
end MakeFavService