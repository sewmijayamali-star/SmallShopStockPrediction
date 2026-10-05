package com.ssps
import com.ssps.config.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
        get("/health/db") {
            try {
                MongoDatabase.database.runCommand(
                    org.bson.Document("ping", 1)
                )

                call.respondText("MongoDB Connected Successfully!")
            } catch (e: Exception) {
                call.respondText(
                    "MongoDB Connection Failed: ${e.message}"
                )
            }
        }
    }
}