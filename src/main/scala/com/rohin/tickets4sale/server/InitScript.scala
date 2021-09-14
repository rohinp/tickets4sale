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
import com.mongodb.client.MongoCollection

object InitScript:
  //default port 29019 
  lazy val embededMongo:EmbeddedMongoDB = 
    EmbeddedMongoDB.create().start()

  def mongoClient:EmbeddedMongoDB => MongoClient = 
    mongo => new MongoClient(mongo.getHost(), mongo.getPort());

  def mongoDB(using c:Config):MongoClient => MongoDatabase = 
    _.getDatabase(c.getString("tickets4Sale.db"))

  def initDB(using c:Config):MongoDatabase =
    embededMongo
      .pipe(mongoClient)
      .pipe(mongoDB)

end InitScript