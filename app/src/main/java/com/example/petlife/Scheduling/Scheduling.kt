package com.example.petlife.Scheduling

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


data class TaskCheck(
    val Listname: String,
    val Checkedlist: Boolean = false,
)

@Composable
fun Scheduling(navController: NavHostController) {
    Box(
        modifier = Modifier
            .background(Color(0xFFF3978F6)),
    ) {
        Button(
            onClick = {
                navController.navigate("Home")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D6498),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(
                text = "Voltar ",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                ),
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(20.dp)
                    .width(10.dp)
                    .padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        )
        {

            var textInput by remember { mutableStateOf("") }
            val addTask = remember {
                mutableStateListOf<TaskCheck>()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = {
                            Text(
                                "Digite nome do medicamento",
                                color = Color.White
                            )
                        },
                        modifier = Modifier
                            .weight(1f) ,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Yellow,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.Yellow,
                            focusedContainerColor = Color(0xFF2D2D2D),
                            unfocusedContainerColor = Color(0xFF2D2D2D)
                        ),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                addTask.add(TaskCheck(Listname = textInput))
                                textInput = ""
                            }
                        },
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2D6498),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Adicionar")
                    }
                }
                    Spacer(
                        modifier = Modifier
                            .height(24.dp)
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF2D6498))
                            .size(400.dp)
                    )
                    {

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            addTask.forEachIndexed { index, task ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(60.dp)
                                            .background(Color(0xFFF2D6498))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFFFC0C2C9)),
                                        ) {
                                            Text(
                                                text = task.Listname,
                                                textAlign = TextAlign.Center,
                                                color = Color.White,
                                                fontSize = 30.sp,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

