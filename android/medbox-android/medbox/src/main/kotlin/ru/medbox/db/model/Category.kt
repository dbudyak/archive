package ru.medbox.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Category(
        @field:PrimaryKey
        val id: Int,
        val title: String,
        val description: String
)