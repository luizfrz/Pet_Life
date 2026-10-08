package com.example.petlife.presentation.scheduling

import com.example.petlife.domain.model.Medication

data class SchedulingUiState(
    val medications: List<Medication> = emptyList(),
    val petName: String = "",
    val medicationName: String = "",
    val hour: Int = 8,
    val minute: Int = 0,
    /** Id do agendamento em edição; `null` quando o formulário cria um novo. */
    val editingMedicationId: Long? = null,
    val exactAlarmAllowed: Boolean = true,
    val medicationPendingDelete: Medication? = null,
    val errorMessage: String? = null
)
