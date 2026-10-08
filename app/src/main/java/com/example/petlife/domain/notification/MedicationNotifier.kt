package com.example.petlife.domain.notification

import com.example.petlife.domain.model.Medication

interface MedicationNotifier {
    /** Confirmação exibida logo após agendar um medicamento. */
    fun showScheduled()

    /** Lembrete exibido na hora do medicamento. */
    fun showReminder(medication: Medication)
}
