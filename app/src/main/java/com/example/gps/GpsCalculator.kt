package com.example.gps

import com.example.model.CompassDirection
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class GpsPoint(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)

object GpsCalculator {

    // Earth radius in meters
    const val EARTH_RADIUS_METERS = 6371000.0
    const val FEET_PER_METER = 3.2808398950131

    // Haversine formula returning distance in feet
    fun haversineDistanceFeet(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)

        val a = sin(dLat / 2.0) * sin(dLat / 2.0) +
                cos(rLat1) * cos(rLat2) * sin(dLon / 2.0) * sin(dLon / 2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))

        val distanceMeters = EARTH_RADIUS_METERS * c
        return distanceMeters * FEET_PER_METER
    }

    // Calculates compass bearing in degrees (0..360)
    fun calculateBearing(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)
        val dLon = Math.toRadians(lon2 - lon1)

        val y = sin(dLon) * cos(rLat2)
        val x = cos(rLat1) * sin(rLat2) - sin(rLat1) * cos(rLat2) * cos(dLon)
        val bearingRad = atan2(y, x)
        val bearingDeg = Math.toDegrees(bearingRad)
        return (bearingDeg + 360.0) % 360.0
    }

    // Maps bearing degrees to 8-point compass direction
    fun bearingToCompassDirection(bearingDeg: Double): CompassDirection {
        val b = (bearingDeg % 360.0 + 360.0) % 360.0
        return when {
            b >= 337.5 || b < 22.5 -> CompassDirection.NORTH
            b >= 22.5 && b < 67.5 -> CompassDirection.NORTH_EAST
            b >= 67.5 && b < 112.5 -> CompassDirection.EAST
            b >= 112.5 && b < 157.5 -> CompassDirection.SOUTH_EAST
            b >= 157.5 && b < 202.5 -> CompassDirection.SOUTH
            b >= 202.5 && b < 247.5 -> CompassDirection.SOUTH_WEST
            b >= 247.5 && b < 292.5 -> CompassDirection.WEST
            else -> CompassDirection.NORTH_WEST
        }
    }

    // Shoelace formula to compute polygon area in square feet from GPS vertices
    fun shoelaceAreaSqFeet(points: List<GpsPoint>): Double {
        if (points.size < 3) return 0.0

        val lat0 = points[0].latitude
        val lon0 = points[0].longitude
        val rLat0 = Math.toRadians(lat0)

        // Project lat/lon to local Cartesian coordinate offsets (x, y) in feet
        val xCoords = DoubleArray(points.size)
        val yCoords = DoubleArray(points.size)

        for (i in points.indices) {
            val dLat = Math.toRadians(points[i].latitude - lat0)
            val dLon = Math.toRadians(points[i].longitude - lon0)
            xCoords[i] = (dLon * EARTH_RADIUS_METERS * cos(rLat0)) * FEET_PER_METER
            yCoords[i] = (dLat * EARTH_RADIUS_METERS) * FEET_PER_METER
        }

        var sum1 = 0.0
        var sum2 = 0.0
        val n = points.size

        for (i in 0 until n) {
            val next = (i + 1) % n
            sum1 += xCoords[i] * yCoords[next]
            sum2 += yCoords[i] * xCoords[next]
        }

        return kotlin.math.abs(sum1 - sum2) / 2.0
    }
}
