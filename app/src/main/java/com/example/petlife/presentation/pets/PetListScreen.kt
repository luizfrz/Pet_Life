package com.example.petlife.presentation.pets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.petlife.R
import com.example.petlife.presentation.components.BackButton
import com.example.petlife.presentation.components.PetBlack
import com.example.petlife.presentation.components.PetGray

@Composable
fun PetListScreen(navController: NavHostController) {
    Column(Modifier.fillMaxSize().background(Color.White).statusBarsPadding()) {
        Box(Modifier.fillMaxWidth().height(190.dp)) {
            Image(
                painterResource(R.drawable.pet_logo_form),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(top = 24.dp, bottom = 8.dp)
            )
            BackButton({ navController.popBackStack() }, Modifier.padding(start = 12.dp, top = 12.dp))
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 45.dp, topEnd = 45.dp))
                .background(PetBlack)
        ) {
            if (PetStore.pets.isEmpty()) {
                Text(
                    "Nenhum pet cadastrado",
                    style = TextStyle(color = Color.White, fontSize = 22.sp),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            LazyColumn(
                modifier = Modifier.navigationBarsPadding(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(PetStore.pets) { PetRow(it) }
            }
        }
    }
}

@Composable
private fun PetRow(pet: Pet) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        // No photo in the data model yet: initial on a gray circle instead of the Figma sample photos.
        Box(Modifier.size(100.dp).clip(CircleShape).background(PetGray), Alignment.Center) {
            Text(pet.name.take(1).uppercase(), style = TextStyle(color = Color.Black, fontSize = 40.sp))
        }
        Column(Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pet.name,
                    style = TextStyle(color = Color.White, fontSize = 22.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    pet.species,
                    style = TextStyle(color = Color.Black, fontSize = 16.sp),
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(PetGray).padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }
            Text(
                pet.age.ifBlank { "-" },
                style = TextStyle(color = Color.Black, fontSize = 22.sp),
                modifier = Modifier.padding(top = 6.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PetGray).padding(16.dp)
            )
        }
    }
}
