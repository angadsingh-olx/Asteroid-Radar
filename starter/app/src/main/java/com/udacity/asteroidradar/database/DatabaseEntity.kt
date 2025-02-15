package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.domain.Asteroid

@Entity(tableName = "asteroid")
data class AsteroidDBEntity(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun List<AsteroidDBEntity>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid (
            it.id, it.codename, it.closeApproachDate, it.absoluteMagnitude,
            it.estimatedDiameter, it.relativeVelocity, it.distanceFromEarth,
            it.isPotentiallyHazardous
        )
    }
}

fun List<Asteroid>.asDatabaseModel(): List<AsteroidDBEntity> {
    return map {
        AsteroidDBEntity(
            it.id, it.codename, it.closeApproachDate, it.absoluteMagnitude,
            it.estimatedDiameter, it.relativeVelocity, it.distanceFromEarth,
            it.isPotentiallyHazardous
        )
    }
}