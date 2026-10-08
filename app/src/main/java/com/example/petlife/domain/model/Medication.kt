package com.example.petlife.domain.model

data class Medication(
    val id: Long = 0,
    val petName: String,
    val medicationName: String,
    val hour: Int,
    val minute: Int,
    /** Marcado pelo usuário quando a dose do dia foi dada; reiniciado a cada disparo do alarme. */
    val taken: Boolean = false
) {
    val timeLabel: String get() = "%02d:%02d".format(hour, minute)
}
