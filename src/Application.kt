package de.mayflower

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import com.github.javafaker.Faker
import io.nats.client.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val natsConnection: Connection = Nats.connect()
    natsConnection.publish("backend.hello", "Hello NATS Event Bus from Backend!".toByteArray())
    natsConnection.createDispatcher { message ->
        val joke = Faker.instance().chuckNorris().fact()
        natsConnection.publish("backend.joked", joke.toByteArray())
    }.subscribe("backend.joke")

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respondText("Hello Backend!", contentType = ContentType.Text.Plain)
        }
        get("/joke") {
            val chuck = Faker.instance().chuckNorris()

            val status = "success"
            val id = chuck.hashCode()
            val joke = chuck.fact()

            call.respond(
                mapOf(
                    "status" to status,
                    "value" to mapOf(
                        "id" to id,
                        "joke" to joke
                    )
                )
            )
        }
    }
}
