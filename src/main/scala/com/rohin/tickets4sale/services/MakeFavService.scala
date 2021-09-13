package com.rohin.tickets4sale.services

import cats.effect.IO
import com.rohin.tickets4sale.db.Tickets4SaleRepo
import com.rohin.tickets4sale.core.domain._

trait MakeFavService[F[_]]:
  def updateFav(fav:FavTitle):F[Int]

object MakeFavService:
  def impl(ticketsRepo:Tickets4SaleRepo[IO]): MakeFavService[IO] = new MakeFavService[IO]{
    def updateFav(fav:FavTitle):IO[Int] = 
      ticketsRepo.findPerformacesByTitle(fav.title)
        .flatMap(ps => if ps.exists(_.favrouite != fav.isFav) then ticketsRepo.updatePerformaces(ps.map(_.copy(favrouite = fav.isFav))) else IO(0))
  }
end MakeFavService