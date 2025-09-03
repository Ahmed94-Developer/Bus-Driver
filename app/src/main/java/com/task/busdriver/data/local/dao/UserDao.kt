package com.task.busdriver.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.task.busdriver.domain.entities.LoginParams
import com.task.busdriver.domain.entities.TripEntity
import com.task.busdriver.domain.entities.TripPointEntity


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(params: LoginParams)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUser(username: String): LoginParams?

    @Insert fun insertTrip(trip: TripEntity): Long
    @Insert fun insertPoints(points: List<TripPointEntity>)
    @Transaction
    suspend fun insertFullTrip(trip: TripEntity, points: List<TripPointEntity>) {
        val tripId = insertTrip(trip)
        val pointsWithTripId = points.map { it.copy(tripId = tripId.toString()) }
        insertPoints(pointsWithTripId)
    }
}