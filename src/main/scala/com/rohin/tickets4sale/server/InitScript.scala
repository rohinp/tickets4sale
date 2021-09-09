package com.rohin.tickets4sale.server

import de.svenkubiak.embeddedmongodb.EmbeddedMongoDB
import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.rohin.tickets4sale.core.domain._
import io.circe.generic.auto._
import io.circe.syntax._
import org.bson.Document
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import scala.util.chaining._
import org.log4s.getLogger

object InitScript:

  lazy val logger = getLogger

  lazy val initMongo:EmbeddedMongoDB = 
    //default port 29019 
    EmbeddedMongoDB.create().start()

  def initMongoClient:EmbeddedMongoDB => MongoClient = 
    mongo => new MongoClient(mongo.getHost(), mongo.getPort());

  def initDB:Config => MongoClient => MongoDatabase = 
    c => _.getDatabase(c.getString("tickets4Sale.db"))

  def insertDefaults:Config => MongoDatabase => MongoDatabase =
    config => mongo =>
      val prices = mongo.getCollection(config.getString("tickets4Sale.tables.prices"))
      prices.insertOne(Document.parse(Price(Genre.Musicals, 70).asJson.toString))
      prices.insertOne(Document.parse(Price(Genre.Comedies, 50).asJson.toString))
      prices.insertOne(Document.parse(Price(Genre.Dramas, 40).asJson.toString))

      mongo

  def testIfDataInserted:Config => MongoDatabase => MongoDatabase =
    c => md => 
      val table = md.getCollection(c.getString("tickets4Sale.tables.prices"))
      import io.circe.parser.decode
      table.find.forEach{
        r => logger.info(decode[Price](r.toJson).toString)
      }
      md


  def initializeDB:Config => MongoDatabase = c =>
    initMongo
          .pipe(initMongoClient)
          .pipe(initDB(c))
          .pipe(insertDefaults(c))
          .pipe(testIfDataInserted(c))

end InitScript