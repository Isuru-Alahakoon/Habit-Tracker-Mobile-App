package com.leolithy.exam_06.data.model

import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var isCompleted: Boolean = false
)