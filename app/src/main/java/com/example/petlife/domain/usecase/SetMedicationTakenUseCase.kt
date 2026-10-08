package com.example.petlife.domain.usecase

import com.example.petlife.domain.repository.MedicationRepository

class SetMedicationTakenUseCase(private val repository: MedicationRepository) {
    suspend operator fun invoke(medicationId: Long, taken: Boolean) = repository.setTaken(medicationId, taken)
}
