package com.rohin.tickets4sale.routes

import com.rohin.tickets4sale.services._
import com.rohin.tickets4sale.core.domain._
import cats.effect._
import cats.syntax.all._
import fs2.Stream
import fs2.text
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers._
import org.http4s.multipart.Multipart
import org.http4s.ember.server._
import org.http4s.syntax.all._
import org.http4s._
import scala.concurrent.duration._
import org.apache.commons.csv.CSVFormat
import java.io.StringReader
import collection.JavaConverters._
import scala.concurrent.Future
import cats.effect.IO
import java.time.LocalDate

object Tickets4saleRoutes {

  def indexRoutes[F[_]: Async](I: Index[F]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root =>
        for {
          index <- I.index
          resp <- Ok(index, `Content-Type`(MediaType.text.html))
        } yield resp
    }

  def inventoryRoutes[F[_]: Async](I: InventoryService[IO]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    given QueryParamDecoder[LocalDate] = QueryParamDecoder[String].map(LocalDate.parse)
    object ShowDateParamMatcher extends QueryParamDecoderMatcher[LocalDate]("show-date")
    object QueryDateParamMatcher extends QueryParamDecoderMatcher[LocalDate]("query-date")

    HttpRoutes.of[F] {
      case GET -> Root / "show" :? ShowDateParamMatcher(showDate) +& QueryDateParamMatcher(queryDate)  =>
        //This is bad, but wanted a quick fix as I was running out of time ;-)
        //Although it's on boundry
        import cats.effect.unsafe.implicits.global
        I.inventory(queryDate,showDate)
          .map(in => Ok(in.asJson)).unsafeRunSync()
    }

  def fileLoaderRoutes[F[_]: Async](H: TicketsUpload[IO]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    HttpRoutes.of[F] {
      case req @ PUT -> Root / "inventory" =>
        req.decode[Multipart[F]]{ m =>
          m.parts.find(_.name.contains("dataFile")) match 
            case None => 
              BadRequest(s"File not found.")
            case Some(part) => 
              val records = part.body.through(text.utf8.decode)
                .map(i => H.bulkUpload(CSVFormat.EXCEL.parse(new StringReader(i))))
              for 
                contents <- records.compile.foldMonoid
                response <- Ok("Success")
              yield response
        }
    }

  def markFavrouiteRoutes[F[_]: Async](H: MakeFavService[IO]): HttpRoutes[F] =
    val dsl = new Http4sDsl[F]{}
    import dsl.*
    import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
    import cats.effect.unsafe.implicits.global
    HttpRoutes.of[F] {
      case req @ POST -> Root / "fav" =>
        for {
          fav <- req.as[FavTitle].map(f => H.updateFav(f))
          resp <- Ok(fav.unsafeRunSync().toString)
        } yield (resp)
    }

}
