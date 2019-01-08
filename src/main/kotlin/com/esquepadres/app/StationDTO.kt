package com.esquepadres.app

data class StationDTO(val id: Int, val name: String, val bikes: Int, val locks: Int, val inService: Boolean, val location: Point)

data class Point(val longitude: Double, val latitude: Double)