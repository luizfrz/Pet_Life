package com.example.petlife.presentation.scheduling

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.petlife.alarm.MedicationAlarmScheduler
import com.example.petlife.alarm.NotificationHelper
import com.example.petlife.data.MedicationRepository

private val Blue = Color(0xFF2D6498)
private val Surface = Color(0xFF2D2D2D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { MedicationRepository(context) }
    val medications = remember { mutableStateListOf<com.example.petlife.data.Medication>().apply { addAll(repository.getAll()) } }

    var petName by remember { mutableStateOf("") }
    var medicationName by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf(8) }
    var minute by remember { mutableStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }
    var exactAllowed by remember { mutableStateOf(MedicationAlarmScheduler.canScheduleExact(context)) }

    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !NotificationHelper.hasPermission(context)) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .padding(24.dp)
    ) {
        Text(
            text = "Agendamentos",
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (!exactAllowed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF5A3A1A))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Sem permissão de alarme exato: o aviso pode atrasar alguns minutos.",
                        style = TextStyle(color = Color.White, fontSize = 14.sp)
                    )
                    TextButton(onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                .setData(android.net.Uri.parse("package:${context.packageName}"))
                        )
                    }) { Text("Permitir") }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FormField(petName, "Nome do pet") { petName = it }
            FormField(medicationName, "Medicamento") { medicationName = it }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Horário: %02d:%02d".format(hour, minute), color = Color.White) }
                Button(
                    onClick = {
                        if (petName.isNotBlank() && medicationName.isNotBlank()) {
                            val medication = repository.add(petName.trim(), medicationName.trim(), hour, minute)
                            MedicationAlarmScheduler.schedule(context, medication)
                            medications.add(medication)
                            exactAllowed = MedicationAlarmScheduler.canScheduleExact(context)
                            petName = ""
                            medicationName = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue)
                ) { Text("Agendar") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(medications, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${item.petName} - ${item.medicationName}",
                                style = TextStyle(color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Todo dia às ${item.timeLabel}",
                                style = TextStyle(color = Color.Gray, fontSize = 14.sp)
                            )
                        }
                        TextButton(onClick = {
                            MedicationAlarmScheduler.cancel(context, item.id)
                            repository.remove(item.id)
                            medications.remove(item)
                        }) { Text("Remover", color = Color(0xFFE57373)) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) {
            Text("Voltar", style = TextStyle(fontWeight = FontWeight.Bold))
        }
    }

    if (showTimePicker) {
        val state = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    hour = state.hour
                    minute = state.minute
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") } },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) { TimePicker(state = state) }
            }
        )
    }
}

@Composable
private fun FormField(value: String, label: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Blue,
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface
        ),
        singleLine = true
    )
}
