package com.example.petlife.presentation.pets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.petlife.R
import com.example.petlife.presentation.components.BackButton
import com.example.petlife.presentation.components.PetBlack
import com.example.petlife.presentation.components.PetButton
import com.example.petlife.presentation.components.PetField

private val GENDERS = listOf("Macho", "Fêmea")
private val SPECIES = listOf("Canino", "Felino", "Roedor", "Outro")

@Composable
fun PetRegisterScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    val canSave = name.isNotBlank() && gender.isNotEmpty() && species.isNotEmpty()

    Box(Modifier.fillMaxSize().background(Color.White).statusBarsPadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painterResource(R.drawable.pet_logo_form),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().height(160.dp)
            )
            Label("Nome do pet"); PetField(name, { name = it })
            Label("Idade"); PetField(age, { age = it })
            Label("Genero"); PetSelect(gender, GENDERS) { gender = it }
            Label("Especie"); PetSelect(species, SPECIES) { species = it }
            PetButton(
                "Salvar",
                onClick = {
                    if (canSave) {
                        PetStore.pets.add(Pet(name.trim(), age.trim(), gender, species))
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.padding(top = 16.dp),
                color = if (canSave) Color(0xFF131313) else Color.Gray
            )
        }
        BackButton({ navController.popBackStack() }, Modifier.padding(start = 12.dp, top = 12.dp))
    }
}

@Composable
private fun Label(text: String) =
    Text(text, style = TextStyle(color = Color.Black, fontSize = 22.sp), modifier = Modifier.padding(top = 8.dp))

/** Black box with a white square + triangle, like the Figma dropdowns. */
@Composable
private fun PetSelect(value: String, options: List<String>, onPick: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        Box(
            Modifier
                .fillMaxWidth()
                .height(69.dp)
                .clip(RoundedCornerShape(17.dp))
                .background(PetBlack)
                .clickable(role = Role.DropdownList) { open = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(Modifier.size(width = 31.dp, height = 27.dp).background(Color.White), Alignment.Center) {
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.Black)
            }
            Text(value, style = TextStyle(color = Color.White, fontSize = 22.sp), modifier = Modifier.padding(start = 48.dp))
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            options.forEach { o -> DropdownMenuItem(text = { Text(o) }, onClick = { onPick(o); open = false }) }
        }
    }
}
