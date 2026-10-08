package com.example.petlife.data

data class Medication(
    val id: Int,
    val petName: String,
    val medicationName: String,
    val hour: Int,
    val minute: Int
) {
    val timeLabel: String get() = "%02d:%02d".format(hour, minute)
}
