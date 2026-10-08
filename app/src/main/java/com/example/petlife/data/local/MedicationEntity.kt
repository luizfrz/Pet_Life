package com.example.petlife.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petName: String,
    val medicationName: String,
    val hour: Int,
    val minute: Int,
    val taken: Boolean = false
)
