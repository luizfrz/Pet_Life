package com.example.petlife.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medications ORDER BY hour, minute")
    fun observeAll(): Flow<List<MedicationEntity>>

    @Query("SELECT * FROM medications ORDER BY hour, minute")
    suspend fun getAll(): List<MedicationEntity>

    @Query("SELECT * FROM medications WHERE id = :id")
    suspend fun getById(id: Long): MedicationEntity?

    @Insert
    suspend fun insert(entity: MedicationEntity): Long

    @Update
    suspend fun update(entity: MedicationEntity)

    @Query("UPDATE medications SET taken = :taken WHERE id = :id")
    suspend fun setTaken(id: Long, taken: Boolean)

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteById(id: Long)
}
