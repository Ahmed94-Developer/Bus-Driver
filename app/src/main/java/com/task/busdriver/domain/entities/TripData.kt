package com.task.busdriver.domain.entities

data class TripData(
    val tripId: String = "",
    val driverId: String = "",
    val locations: List<TripPointEntity> = emptyList(),
    val startedAt: Long = 0L,
    val endedAt: Long = 0L,
    val totalDistanceKm: Double = 0.0
)