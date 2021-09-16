package com.rohin.tickets4sale.core.domain

import Performace._
import java.time.LocalDate

class PerformancesSpec extends munit.FunSuite:
  test("Generating date range"){
    val startDate = LocalDate.parse("2021-10-13")
    val numberOfElements = 3
    val result = dateRange(startDate,numberOfElements).toList
    val expected = List(startDate,startDate.plusDays(1),startDate.plusDays(2))
    assertEquals(result, expected)
  }

  test("Generating performaces based on start date"){
    val input = RawPerformace(Genre.COMEDY, "Matrix", LocalDate.now)
    val startDate = LocalDate.now
    val result = generatePerformaces(3)(input)
    assertEquals(result.map(_.showDate),List(startDate,startDate.plusDays(1),startDate.plusDays(2)))
    assertEquals(result.map(_.ticketsLeft),List.fill(3)(200))
    assertEquals(result.map(_.maxPurchase),List.fill(3)(10))
  }

  test("Generated performaces after 60"){
      val startDate = LocalDate.parse("2021-10-13")
      val input = RawPerformace(Genre.COMEDY, "Matrix", startDate)
      val result = generatePerformaces(100)(input)
      assertEquals(result.map(_.ticketsLeft)(61),100)
      assertEquals(result.length,100)
    }
end PerformancesSpec