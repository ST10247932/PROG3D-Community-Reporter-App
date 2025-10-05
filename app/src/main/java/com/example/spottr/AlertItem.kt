package com.example.spottr


data class AlertItem(
    val name: String,
    var isEnabled: Boolean = true,
    var category: String = "General",
    var sound: String = "Default",
    var priority: String = "Medium"
)