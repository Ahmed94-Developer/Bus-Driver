package com.task.busdriver.domain.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class LoginParams(
    @PrimaryKey val username: String,
    val passwordHash: String
)