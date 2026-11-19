package com.example.petlife.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Figma frames are ~4x the dp size (1651px wide -> ~412dp).
val PetBlack = Color.Black
val PetGray = Color(0xFFE2E2E2)

/** Black pill with a "<" like the Figma "botao voltar". */
@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(56.dp)
            .height(40.dp) // >= 40dp touch target (Figma's 26dp was too small to tap)
            .clip(RoundedCornerShape(10.dp))
            .background(PetBlack)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Voltar",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

/** Big black rounded button used on Home and forms. */
@Composable
fun PetButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = PetBlack) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(69.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(color)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = TextStyle(color = Color.White, fontSize = 22.sp))
    }
}

/** Black rounded text field with white text (Figma "menta" inputs). */
@Composable
fun PetField(value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    BasicTextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        textStyle = TextStyle(color = Color.White, fontSize = 22.sp),
        cursorBrush = SolidColor(Color.White),
        modifier = modifier
            .fillMaxWidth()
            .height(69.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(PetBlack),
        decorationBox = { inner ->
            Box(Modifier.padding(horizontal = 24.dp), contentAlignment = Alignment.CenterStart) { inner() }
        }
    )
}
