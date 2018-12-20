package ru.medbox.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Specialization(
        @field:PrimaryKey
        val id: Int,
        val name: String,
        val description: String,
        val imageUrl: String
)