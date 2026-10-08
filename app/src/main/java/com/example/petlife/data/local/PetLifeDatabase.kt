package com.example.petlife.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MedicationEntity::class], version = 1, exportSchema = false)
abstract class PetLifeDatabase : RoomDatabase() {
    abstract fun medicationDao(): MedicationDao
}
