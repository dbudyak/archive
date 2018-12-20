package ru.medbox.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Feedback(
        @PrimaryKey
        val id: Int,
        val userName: String,
        val message: String,
        val rating: Int
)