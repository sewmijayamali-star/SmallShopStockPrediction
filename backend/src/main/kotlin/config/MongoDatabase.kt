package com.ssps.config

import com.mongodb.kotlin.client.coroutine.MongoClient

object MongoDatabase {

    private val client: MongoClient =
        MongoClient.create(System.getenv("MONGODB_URI"))

    val database = client.getDatabase("ssps")
}