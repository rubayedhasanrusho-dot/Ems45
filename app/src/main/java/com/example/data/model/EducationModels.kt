package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "education_categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String, // "HSC", "SSC", "ALIM", "DAKHIL", "JSC", "OTHER"
    val name: String,
    val description: String,
    val iconName: String,
    val orderIndex: Int
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String, // "HSC", "SSC", etc.
    val groupName: String, // "Science", "Business Studies", "Humanities", "General"
    val name: String,
    val code: String,
    val totalChapters: Int = 10,
    val colorHex: String = "#6345ED"
)

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey
    val id: String,
    val subjectId: String,
    val chapterNumber: Int,
    val title: String,
    val questionCount: Int = 25
)
