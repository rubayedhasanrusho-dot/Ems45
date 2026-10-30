package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val passwordHash: String = "password123",
    val institution: String,
    val educationLevel: String, // e.g., "HSC - Science", "SSC", "Alim"
    val role: String = "STUDENT", // "STUDENT" or "ADMIN"
    val avatarUrl: String = "",
    val points: Int = 1250,
    val isPhoneVerified: Boolean = true,
    val isEmailVerified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
