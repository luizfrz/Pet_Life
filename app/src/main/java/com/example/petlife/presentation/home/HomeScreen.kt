package com.example.petlife.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.petlife.R
import com.example.petlife.presentation.components.PetButton

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(R.drawable.pet_logo_home),
            contentDescription = "Pet Life",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(160.dp)
        )
        Text(
            "Agende com facilidade medicamento do seu pet",
            style = TextStyle(color = Color(0xFF010101), fontSize = 22.sp, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 32.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            PetButton("Pets", { navController.navigate("PetList") })
            PetButton("Cadastrar pet", { navController.navigate("PetRegister") })
            PetButton("Criar Agendamento", { navController.navigate("Scheduling") })
        }
    }
}
