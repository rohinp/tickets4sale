
import io.circe.Decoder
import io.circe.HCursor
import io.circe.Encoder
import io.circe.syntax._
import com.rohin.tickets4sale.core.domain._
import com.rohin.tickets4sale.core.domain.RawPerformace.given
import io.circe.generic.auto._
import io.circe.syntax._

import io.circe.parser.decode
import java.time.LocalDate
import scala.util.Try

given Decoder[LocalDate] = Decoder.decodeString.emapTry ( str => Try(LocalDate.parse(str)))
given Encoder[LocalDate] = Encoder.instance(_.toString.asJson)

given Decoder[Genre] = Decoder.decodeString.emapTry(str => Try(Genre.valueOf(str)))
given Encoder[Genre] = Encoder.instance(_.toString.asJson)

val data = """1984,2021-10-13,DRAMA"""

val r = data.split(",")

decode[LocalDate]("2021-10-13")

decode[RawPerformace](
  s"""{"genre": "${r(2)}", "title": "${r(0)}", "showDate": "${r(1)}"}"""
  )


Genre.COMEDY.asJson
decode[Genre](""""genre": {
"DRAMA": {}
}""".asJson.noSpaces)