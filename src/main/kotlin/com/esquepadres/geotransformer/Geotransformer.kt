package com.esquepadres.geotransformer

import org.osgeo.proj4j.*


data class Point(val x: Double, val y: Double, val wkid: Int)

class Geotransformer {

    private val crsFactory = CRSFactory()
    private val crsCache = HashMap<Int, CoordinateReferenceSystem>()
    private val transformerCache = HashMap<Pair<Int, Int>, CoordinateTransform>()

    private fun _getCRS(wkid: Int): CoordinateReferenceSystem {
        if(crsCache[wkid] == null) {
            crsCache[wkid] = crsFactory.createFromName("EPSG:$wkid")
        }
        return crsCache[wkid] ?: throw InvalidValueException("No such wkid found (wkid: $wkid)")
    }

    private fun _getTransformer(fromWkid: Int, toWkid: Int): CoordinateTransform {
        val key = Pair(fromWkid, toWkid)

        if(transformerCache[key] == null) {
            val fromCrs = _getCRS(fromWkid)
            val toCrs = _getCRS(toWkid)

            transformerCache[key] = BasicCoordinateTransform(fromCrs, toCrs)
        }
        return transformerCache[key] ?: throw InvalidValueException("Could not get transformer from wkid $fromWkid to wkid $toWkid.")
    }

    fun transform(point: Point, toWkid: Int): Point {
        val transformer = _getTransformer(point.wkid, toWkid)

        val fromCoordinate = ProjCoordinate(point.x, point.y)
        val toCoordinate = ProjCoordinate()

        transformer.transform(fromCoordinate, toCoordinate)

        return Point(toCoordinate.x, toCoordinate.y, toWkid)
    }
}