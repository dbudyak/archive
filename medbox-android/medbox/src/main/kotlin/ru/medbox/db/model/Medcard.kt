package ru.medbox.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Medcard(
        @PrimaryKey
        val id: Int,
        val emrId: Int,
        val data: String,
        val dateTime: String
)