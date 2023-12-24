package com.example.simplegalleryapp.domain.model

data class Album(
    val id: Long = 0,
    val label: String,
    val relativePath: String,
    val timestamp: Long,
    var count: Long = 0,
)
