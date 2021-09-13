package com.rohin.tickets4sale.services

import cats.implicits.*
import cats.effect.IO
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.*
import java.time.LocalDate
import com.rohin.tickets4sale.core.domain.Inventory
import com.rohin.tickets4sale.db.Tickets4SaleRepo

trait InventoryService[F[_]]{
  def inventory(queryDate:LocalDate, showDate:LocalDate): F[Inventory]
}

object InventoryService {
  def impl(ticketsRepo:Tickets4SaleRepo[IO]): InventoryService[IO] = new InventoryService[IO]{
    def inventory(queryDate:LocalDate, showDate:LocalDate): IO[Inventory] = 
      ticketsRepo.findPerformacesByDate(showDate)
        .map(Inventory.toInventory(queryDate))
  }
}
