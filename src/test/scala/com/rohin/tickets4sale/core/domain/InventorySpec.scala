package com.rohin.tickets4sale.core.domain

import Inventory._
import java.time.LocalDate

class InventorySpec extends munit.FunSuite:
  val nowDate = LocalDate.parse("2021-10-13")
  val input = List(
    RawPerformace(Genre.DRAMA, "Matrix", nowDate.plusDays(5)),
    RawPerformace(Genre.DRAMA, "Matrix", nowDate.plusDays(10)),
    RawPerformace(Genre.DRAMA, "Matrix", nowDate.plusDays(20)),
    RawPerformace(Genre.DRAMA, "Matrix", nowDate),
  )

  test("convertion of performance to inventory"){
    val startDate = LocalDate.parse("2021-10-13")
    val numberOfElements = 3
    val result = dateRange(startDate,numberOfElements).toList
    val expected = List(startDate,startDate.plusDays(1),startDate.plusDays(2))
    assertEquals(result, expected)
  }


  end InventorySpec