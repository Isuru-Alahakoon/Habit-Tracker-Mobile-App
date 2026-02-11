package com.leolithy.exam_06.data.model

import java.util.UUID

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val emoji: String,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)