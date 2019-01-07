package com.esquepadres.externalapi

data class BysykkelStationResponse(val stations: Array<Station>)
data class BysykkelStatusResponse(val status: StationStatus)
data class BysykkelAvailabilityResponse(val stations: Array<StationsAvailability>, val updated_at: String, val refresh_rate: Int)

data class Station(val id: Int, val in_service: Boolean, val title: String, val subtitle: String, val number_of_locks: Int, val center: Coordinate, val bounds: Array<Coordinate>)
data class Coordinate(val latitude: Double, val longitude: Double)

data class StationsAvailability(val id: Int, val availability: Availability)
data class Availability(val bikes: Int, val locks: Int)

data class StationStatus(val all_stations_closed: Boolean, val stations_closed: Array<Int>)
