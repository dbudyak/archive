package ru.medbox.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Lecture(
        @field:PrimaryKey
        val id: Int,
        val title: String,
        val videoUrl: String,
        val thumbUrl: String,
        val content: String
)