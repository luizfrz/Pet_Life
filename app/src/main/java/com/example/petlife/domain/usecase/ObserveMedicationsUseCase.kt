package com.example.petlife.domain.usecase

import com.example.petlife.domain.model.Medication
import com.example.petlife.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.Flow

class ObserveMedicationsUseCase(private val repository: MedicationRepository) {
    operator fun invoke(): Flow<List<Medication>> = repository.observeAll()
}
