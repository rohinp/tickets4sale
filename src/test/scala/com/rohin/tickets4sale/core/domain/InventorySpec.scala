package com.rohin.tickets4sale.core.domain

import Inventory._
import Status._
import java.time.LocalDate
import scala.util.chaining._

class InventorySpec extends munit.FunSuite:
  val queryDate = LocalDate.parse("2021-10-13")

  val rawPerformace: List[RawPerformace] = List(
    RawPerformace(Genre.DRAMA, "Silver Lining", queryDate.plusDays(5)),
    RawPerformace(Genre.COMEDY, "Zohan", queryDate.plusDays(10)),
    RawPerformace(Genre.MUSICAL, "Frozen", queryDate.plusDays(20)),
    RawPerformace(Genre.DRAMA, "Walking Dead", queryDate.plusDays(25))
  )

  val performaces: List[RawPerformace] => List[Performace] =
    rp =>
      for
        r <- rp
        g <- Performace.generatePerformaces(5)(r)
      yield g

  val byDatePerformace: List[Performace] => LocalDate => List[Performace] =
    ls => d => ls.filter(_.showDate == d)

  val inventories: LocalDate => LocalDate => Inventory =
    sd =>
      qd =>
        rawPerformace
          .pipe(performaces)
          .pipe(byDatePerformace)
          .pipe(_(sd))
          .pipe(toInventory(qd))

  test(
    "convertion of performance to inventory showDate in range(25) of queryDate"
  ) {
    val showDate: LocalDate = LocalDate.parse("2021-10-20")
    val resultSatus =
      inventories(showDate)(queryDate).inventory.head.shows.head.status
    assertEquals(resultSatus, OpenForSale.stringify)
  }

  test("convertion of performance to inventory showDate not in range") {
    val showDate: LocalDate = LocalDate.parse("2021-10-18")
    val queryDate = LocalDate.parse("2021-09-01")
    val resultSatus =
      inventories(showDate)(queryDate).inventory.head.shows.head.status
    assertEquals(resultSatus, SaleNotStarted.stringify)
  }

  test("convertion of performance to inventory showDate in paste") {
    val showDate: LocalDate = LocalDate.parse("2021-10-18")
    val queryDate = LocalDate.parse("2021-10-20")
    val resultSatus =
      inventories(showDate)(queryDate).inventory.head.shows.head.status
    assertEquals(resultSatus, InThePast.stringify)
  }

  test("convertion of performance to inventory showDate in soldout case 1 (5 day before show)") {
    val showDate: LocalDate = LocalDate.parse("2021-10-18")
    val queryDate = LocalDate.parse("2021-10-13")

    val resultSatus =
          inventories(showDate)(queryDate).inventory.head.shows.head.status
    assertEquals(resultSatus, SoldOut.stringify)
  }

  test("convertion of performance to inventory showDate in soldout case 2") {
    val showDate: LocalDate = LocalDate.parse("2021-10-18")
    val queryDate = LocalDate.parse("2021-10-01")
    val inventories =
      rawPerformace
        .pipe(performaces)
        .pipe(byDatePerformace)
        .pipe(_(showDate).map(_.copy(ticketsLeft = 0, ticketsSold = Hall.Big.hallSize)))
        .pipe(toInventory(queryDate))

    val resultSatus = inventories.inventory.head.shows.head.status
    assertEquals(resultSatus, "sold out")
  }

end InventorySpec
