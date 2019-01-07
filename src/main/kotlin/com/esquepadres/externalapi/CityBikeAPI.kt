package com.esquepadres.externalapi

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header

class CityBikeAPI(private val client: HttpClient, private val baseUrl: String, private val apiKey: String) {
    suspend fun getStations() : BysykkelStationResponse {
        val stringResponse =  client.get<String>("$baseUrl/stations") {
            header("Client-Identifier", apiKey)
        }
        return Gson().fromJson(stringResponse, BysykkelStationResponse::class.java)
    }

    suspend fun getAvailability() : BysykkelAvailabilityResponse {
        val stringResponse = client.get<String>("$baseUrl/stations/availability") {
            header("Client-Identifier", apiKey)
        }

        return Gson().fromJson(stringResponse, BysykkelAvailabilityResponse::class.java)
    }
}