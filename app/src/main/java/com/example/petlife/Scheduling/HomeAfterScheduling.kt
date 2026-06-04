package com.example.petlife.Scheduling

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HomeAfterScheduling(navController: NavHostController) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF0F4BB4))
    ){
//        Image(
//            painter = painterResource(id = R.drawable.image),
//            contentDescription = "",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .fillMaxWidth()
//                .width(90.dp)
//                .padding(16.dp)
//        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Button(
                onClick = {
                    navController.navigate("Scheduling")
                },
                modifier = Modifier
                    .width(300.dp) // Define a largura exata
                    .height(80.dp) // Define a altura exata)
                    .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF2D6498),
                    contentColor = Color.White
                )
            )
            {
                Text(
                    text = "Agendar",
                    style = TextStyle (
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                    ),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .width(60.dp)
                        .padding(16.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .background(Color(0xFFF214F9D))
                .align(Alignment.BottomCenter)

        ) {
        Text(
            text = "Agendamento do seu pet! ",
            textAlign = TextAlign.Center,
            style = TextStyle (
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
            ),
            color = Color.White,
            fontSize = 30.sp,
            modifier = Modifier
                .height(90.dp)
                .width(400.dp)
          )
        }
    }
}


