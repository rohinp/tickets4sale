package com.rohin.tickets4sale.core.domain

import Inventory._
import java.time.LocalDate

class InventorySpec extends munit.FunSuite:
  val nowDate = LocalDate.parse("2021-10-13")
  val input = List(
    RawPerformace(Genre.DRAMA, "Silver Lining", nowDate.plusDays(5)),
    RawPerformace(Genre.COMEDY, "Zohan", nowDate.plusDays(10)),
    RawPerformace(Genre.MUSICAL, "Frozen", nowDate.plusDays(20)),
    RawPerformace(Genre.DRAMA, "Walking Dead", nowDate.plusDays(25)),
  )

  test("convertion of performance to inventory"){
    assertEquals(true, true)
  }


end InventorySpec