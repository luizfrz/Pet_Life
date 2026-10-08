package com.example.petlife.data.repository

import com.example.petlife.data.local.MedicationDao
import com.example.petlife.data.local.MedicationEntity
import com.example.petlife.domain.model.Medication
import com.example.petlife.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicationRepositoryImpl(private val dao: MedicationDao) : MedicationRepository {

    override fun observeAll(): Flow<List<Medication>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getAll(): List<Medication> = dao.getAll().map { it.toDomain() }

    override suspend fun get(id: Long): Medication? = dao.getById(id)?.toDomain()

    override suspend fun add(medication: Medication): Medication =
        medication.copy(id = dao.insert(medication.toEntity()))

    override suspend fun update(medication: Medication) = dao.update(medication.toEntity())

    override suspend fun setTaken(id: Long, taken: Boolean) = dao.setTaken(id, taken)

    override suspend fun remove(id: Long) = dao.deleteById(id)
}

private fun MedicationEntity.toDomain() = Medication(id, petName, medicationName, hour, minute, taken)

private fun Medication.toEntity() = MedicationEntity(id, petName, medicationName, hour, minute, taken)
