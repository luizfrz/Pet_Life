package com.example.petlife.presentation.pets

import androidx.compose.runtime.mutableStateListOf

data class Pet(val name: String, val age: String, val gender: String, val species: String)

// shortcut: in-memory only (lost when the process dies), move to Room like medications when pets must persist.
object PetStore {
    val pets = mutableStateListOf<Pet>()
}
