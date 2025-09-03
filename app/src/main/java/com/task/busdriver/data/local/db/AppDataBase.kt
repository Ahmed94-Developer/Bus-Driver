package com.task.busdriver.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.task.busdriver.data.local.dao.UserDao
import com.task.busdriver.domain.entities.LoginParams
import com.task.busdriver.domain.entities.TripEntity
import com.task.busdriver.domain.entities.TripPointEntity

@Database(entities = [LoginParams::class, TripPointEntity::class, TripEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

}