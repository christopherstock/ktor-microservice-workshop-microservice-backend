package de.mayflower

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respondText("Hello Backend!", contentType = ContentType.Text.Plain)
        }
        get("/json") {
            val status = "success"
            val id = 1
            val text = "Example JSON data string"

            call.respond(
                mapOf(
                    "status" to status,
                    "value" to mapOf(
                        "id" to id,
                        "text" to text
                    )
                )
            )
        }
    }
}
