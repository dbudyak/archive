package ru.medbox.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Article(
        @field:PrimaryKey
        val id: Int,
        val title: String,
        val description: String,
        val content: String,
        val imageUrl: String,
        val categoryId: Int,
        val visibility: Boolean
)