package ru.medbox.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Doctor(
        @PrimaryKey
        val id: Int,
        val name: String,
        val rating: Int,
        val bio: String,
        val photoUrl: String,
        val specializationId: Int
)