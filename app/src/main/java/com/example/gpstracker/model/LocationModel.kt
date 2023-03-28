package com.example.gpstracker.model

import org.osmdroid.util.GeoPoint

data class LocationModel(
    val speed: Float = 0.0f,
    val distance: Float = 0.0f,
    val geoPointsList: Array<GeoPoint>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationModel

        if (speed != other.speed) return false
        if (distance != other.distance) return false
        if (!geoPointsList.contentEquals(other.geoPointsList)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = speed.hashCode()
        result = 31 * result + distance.hashCode()
        result = 31 * result + geoPointsList.contentHashCode()
        return result
    }
}