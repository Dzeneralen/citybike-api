package com.esquepadres.app

import com.esquepadres.externalapi.CityBikeAPI
import com.esquepadres.externalapi.Station
import io.ktor.application.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.*
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages

import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.gson.gson
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.lang.IllegalArgumentException
import java.text.DateFormat

fun main(args: Array<String>) {

    val cityBikeApiKey = getEnvironmentVariable("BYSYKKEL_API_KEY") ?: throw IllegalArgumentException("must have bysykkel api key in env variables")
    val cityBikeApiUrl = "https://oslobysykkel.no/api/v1"

    val httpClient = HttpClient(Apache)

    val cityBikeApi = CityBikeAPI(httpClient, cityBikeApiUrl, cityBikeApiKey)

    val server = embeddedServer(Netty, 8080) {
        install(DefaultHeaders)
        install(CORS)
        install(ContentNegotiation) {
            gson {
                setDateFormat(DateFormat.LONG)
            }
        }
        install(StatusPages) {
            exception<Throwable> { cause ->
                // TODO
                println(cause.message)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        routing {
            get("/") {
                val stationsResponse = cityBikeApi.getStations()
                val availabilityResponse = cityBikeApi.getAvailability()

                val stations = stationsResponse.stations
                val stationsAvailability = availabilityResponse.stations

                val availabilityDict = stationsAvailability
                        .associateBy({ s -> s.id }, { s -> s.availability })

                val stationsWithStatus = stations
                        .filter { s -> s.in_service}
                        .map { s ->
                            val status = availabilityDict[s.id]
                            StationDTO(
                                    s.id ,
                                    s.title ,
                                    status?.bikes ?: 0,
                                    status?.locks ?: 0,
                                    s.in_service,
                                    Point(s.center.longitude, s.center.latitude)
                            )
                        }

                call.respond(stationsWithStatus)
            }
        }
    }
    server.start(wait = true)
}

fun getEnvironmentVariable(name: String) : String? {
    return System.getenv(name)
}