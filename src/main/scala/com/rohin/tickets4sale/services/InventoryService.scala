package com.rohin.tickets4sale.services

import cats.Applicative
import cats.implicits.*
import com.rohin.tickets4sale.core.domain.Inventory
import com.rohin.tickets4sale.db.Tickets4SaleRepo
import io.circe.Encoder
import io.circe.Json
import org.http4s.EntityEncoder
import org.http4s.circe.*

import java.time.LocalDate

trait InventoryService[F[_]]{
  def inventory(queryDate:LocalDate, showDate:LocalDate): F[Inventory]
}

object InventoryService {
  def impl[F[_]:Applicative](ticketsRepo:Tickets4SaleRepo[F]): InventoryService[F] = new InventoryService[F]{
    def inventory(queryDate:LocalDate, showDate:LocalDate): F[Inventory] = 
      ticketsRepo.findPerformacesByDate(showDate)
        .map(Inventory.toInventory(queryDate))
  }
}
