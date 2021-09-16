package com.rohin.tickets4sale.server

import com.mongodb.MongoClient
import com.mongodb.client._
import com.rohin.tickets4sale.core.domain._
import com.typesafe.config._
import de.svenkubiak.embeddedmongodb.EmbeddedMongoDB
import io.circe.generic.auto._
import io.circe.syntax._
import org.bson.Document
import org.log4s.getLogger

import scala.util.chaining._
import com.mongodb.BasicDBObject

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