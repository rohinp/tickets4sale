package com.rohin.tickets4sale.services

import cats.effect.IO
import com.rohin.tickets4sale.core.domain._
import com.rohin.tickets4sale.db.Tickets4SaleRepo

trait MakeFavService[F[_]]:
  def updateFav(fav:FavTitle):F[Int]

object MakeFavService:
  def impl[F[_]](ticketsRepo:Tickets4SaleRepo[F]): MakeFavService[F] = new MakeFavService[F]{
    def updateFav(fav:FavTitle):F[Int] = 
      ticketsRepo.updatePerformaces(fav)
  }
end MakeFavService