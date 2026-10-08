package com.example.petlife.domain.repository

import com.example.petlife.domain.model.Medication
import kotlinx.coroutines.flow.Flow

interface MedicationRepository {
    fun observeAll(): Flow<List<Medication>>
    suspend fun getAll(): List<Medication>
    suspend fun get(id: Long): Medication?

    /** Persiste o medicamento e devolve a cópia com o id gerado. */
    suspend fun add(medication: Medication): Medication
    suspend fun update(medication: Medication)
    suspend fun setTaken(id: Long, taken: Boolean)
    suspend fun remove(id: Long)
}
