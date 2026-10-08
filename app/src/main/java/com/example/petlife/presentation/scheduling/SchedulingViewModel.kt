package com.example.petlife.presentation.scheduling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petlife.domain.usecase.AddMedicationUseCase
import com.example.petlife.domain.usecase.CanScheduleExactAlarmsUseCase
import com.example.petlife.domain.usecase.ObserveMedicationsUseCase
import com.example.petlife.domain.model.Medication
import com.example.petlife.domain.usecase.RemoveMedicationUseCase
import com.example.petlife.domain.usecase.SetMedicationTakenUseCase
import com.example.petlife.domain.usecase.UpdateMedicationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SchedulingViewModel(
    observeMedications: ObserveMedicationsUseCase,
    private val addMedication: AddMedicationUseCase,
    private val updateMedication: UpdateMedicationUseCase,
    private val removeMedication: RemoveMedicationUseCase,
    private val setMedicationTaken: SetMedicationTakenUseCase,
    private val canScheduleExactAlarms: CanScheduleExactAlarmsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SchedulingUiState(exactAlarmAllowed = canScheduleExactAlarms()))
    val uiState: StateFlow<SchedulingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeMedications().collect { list -> _uiState.update { it.copy(medications = list) } }
        }
    }

    fun onPetNameChange(value: String) = _uiState.update { it.copy(petName = value) }

    fun onMedicationNameChange(value: String) = _uiState.update { it.copy(medicationName = value, errorMessage = null) }

    fun onTimeChange(hour: Int, minute: Int) = _uiState.update { it.copy(hour = hour, minute = minute) }

    fun onSchedule() {
        val form = _uiState.value
        viewModelScope.launch {
            val result = form.editingMedicationId
                ?.let { updateMedication(it, form.petName, form.medicationName, form.hour, form.minute) }
                ?: addMedication(form.petName, form.medicationName, form.hour, form.minute)
            result
                .onSuccess { _uiState.update { it.clearedForm() } }
                .onFailure { error -> _uiState.update { it.copy(errorMessage = error.message) } }
            refreshExactAlarmPermission()
        }
    }

    /** Carrega o agendamento no formulário para alteração. */
    fun onEdit(medication: Medication) = _uiState.update {
        it.copy(
            petName = medication.petName,
            medicationName = medication.medicationName,
            hour = medication.hour,
            minute = medication.minute,
            editingMedicationId = medication.id,
            errorMessage = null
        )
    }

    fun onCancelEdit() = _uiState.update { it.clearedForm() }

    fun onToggleTaken(medication: Medication, taken: Boolean) {
        viewModelScope.launch { setMedicationTaken(medication.id, taken) }
    }

    fun onRequestDelete(medication: Medication) = _uiState.update { it.copy(medicationPendingDelete = medication) }

    fun onDismissDelete() = _uiState.update { it.copy(medicationPendingDelete = null) }

    fun onConfirmDelete() {
        val medication = _uiState.value.medicationPendingDelete ?: return
        _uiState.update {
            val form = if (it.editingMedicationId == medication.id) it.clearedForm() else it
            form.copy(medicationPendingDelete = null)
        }
        viewModelScope.launch { removeMedication(medication.id) }
    }

    /** Reavalia a permissão de alarme exato (o usuário pode tê-la concedido nas configurações do sistema). */
    fun refreshExactAlarmPermission() = _uiState.update { it.copy(exactAlarmAllowed = canScheduleExactAlarms()) }
}

private fun SchedulingUiState.clearedForm() =
    copy(petName = "", medicationName = "", editingMedicationId = null, errorMessage = null)
