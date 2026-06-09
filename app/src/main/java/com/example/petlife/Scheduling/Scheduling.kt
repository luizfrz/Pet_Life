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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.buildSpannedString
import androidx.navigation.NavHostController
import com.example.petlife.alarm.AlarmScheduler

data class mediCheck(
    val listMedi: String,
    val mediCheck: Boolean = false,
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Scheduling(navController: NavHostController) {
    var textInput by remember { mutableStateOf("") }
    val addMedi = remember { mutableStateListOf<mediCheck>()}
    val contextAlarm = LocalContext.current
    val scheduler = remember {
        AlarmScheduler(contextAlarm)
    }
    var enableDialog by remember {
        mutableStateOf(false)
    }

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
                text = "Tela inicial ",
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
            }
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
                                    "Nome do medicamento",
                                    color = Color.White
                                )
                            },
                            modifier = Modifier
                                .weight(4f),
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
                                    addMedi.add(mediCheck(listMedi = textInput))
                                    textInput = ""
                                }
                            },
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2D6498),
                                contentColor = Color.White
                            )
                        )
                        {
                            Text("Adicionar")
                        }
                        Button(
                            onClick = {
                                enableDialog = true
                            }
                        ) {
                            Text("Alarme")
                        }

                        if (enableDialog) {

                            val timeState = rememberTimePickerState(
                                initialHour = 8,
                                initialMinute = 0,
                                is24Hour = true
                            )

                            AlertDialog(
                                onDismissRequest = {
                                    enableDialog = false
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {

                                            scheduler.schedule(
                                                timeState.hour,
                                                timeState.minute
                                            )

                                            enableDialog = false
                                        }
                                    ) {
                                        Text("Confirmar")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            enableDialog = false
                                        }
                                    ) {
                                        Text("Cancelar")
                                    }
                                },
                                text = {
                                    TimePicker(state = timeState)
                                }
                            )
                        }
                    }

                        Button(
                            onClick = {
                                addMedi.clear()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2D6498),
                                contentColor = Color.White
                            ),
                        ) {
                            Text(
                                text = "Limpar Lista de medicamentos",
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                ),
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .width(10.dp)
                                    .padding(bottom = 5.dp)
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .height(24.dp)
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF4873BF))
                                .size(400.dp)
                        )
                        {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                addMedi.forEachIndexed { index, petMe ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth(),

                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(60.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFFD9D9D9))
                                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                            )
                                            {
                                                Text(
                                                    text = petMe.listMedi,
                                                    modifier = Modifier.padding(
                                                        start = 16.dp,
                                                        end = 16.dp,
                                                        top = 8.dp,
                                                        bottom = 8.dp
                                                    ),
                                                    textAlign = TextAlign.Center,
                                                    color = Color.White,
                                                    fontSize = 30.sp,
                                                )
    //                                            IconButton(
    //                                                onClick = {
    //                                                    addMedi.removeAt(index)
    //                                                }
    //                                            ) {
    //                                                Icon(
    //                                                    imageVector = Icons.Default.Delete,
    //                                                    contentDescription = "Excluir medicamento",
    //                                                    tint = Color.Red,
    //                                                    modifier = Modifier
    //                                                        .size(24.dp)
    //                                                )
    //                                            }
                                             }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }