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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.petlife.PetLifeApp
import com.example.petlife.alarm.SystemMedicationNotifier

private val ScreenBackground = Color.White
private val BoxColor = Color.Black
private val OnBox = Color.White

private const val CLOCK_PATH =
    "M11.99,2C6.47,2 2,6.48 2,12s4.47,10 9.99,10C17.52,22 22,17.52 22,12S17.52,2 11.99,2zM12,20c-4.42,0 -8,-3.58 -8,-8s3.58,-8 8,-8 8,3.58 8,8 -3.58,8 -8,8z" +
        "M12.5,7H11v6l5.25,3.15 0.75,-1.23 -4.5,-2.67z"

private val ClockIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Clock",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).addPath(PathParser().parsePathString(CLOCK_PATH).toNodes(), fill = SolidColor(Color.Black)).build()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulingScreen(
    navController: NavHostController,
    viewModel: SchedulingViewModel = viewModel(factory = schedulingViewModelFactory())
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val goHome: () -> Unit = {
        if (!navController.popBackStack("Home", inclusive = false)) navController.navigate("Home")
    }
    var showTimePicker by remember { mutableStateOf(false) }

    val notificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refreshExactAlarmPermission() }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !SystemMedicationNotifier.hasPermission(context)) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = goHome) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar para a tela inicial",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Agendamentos do seu pet",
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color.Black
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (!state.exactAlarmAllowed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Sem permissão de alarme exato: o aviso pode atrasar alguns minutos.",
                        style = TextStyle(color = Color.Black, fontSize = 14.sp)
                    )
                    TextButton(onClick = {
                        context.startActivity(
                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                .setData(android.net.Uri.parse("package:${context.packageName}"))
                        )
                    }) { Text("Permitir", color = Color.Black) }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FormField(state.petName, "Nome do pet (opcional)", viewModel::onPetNameChange)
            FormField(state.medicationName, "Medicamento", viewModel::onMedicationNameChange)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BoxColor, contentColor = OnBox)
                ) {
                    Icon(
                        ClockIcon,
                        contentDescription = "Definir alarme",
                        tint = OnBox,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("%02d:%02d".format(state.hour, state.minute))
                }
                Button(
                    onClick = viewModel::onSchedule,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BoxColor, contentColor = OnBox)
                ) { Text(if (state.editingMedicationId != null) "Salvar" else "Agendar") }
            }
        }

        if (state.editingMedicationId != null) {
            TextButton(onClick = viewModel::onCancelEdit) { Text("Cancelar edição", color = Color.Black) }
        }

        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, style = TextStyle(color = Color(0xFFB00020), fontSize = 14.sp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.medications, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BoxColor)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.taken,
                            onCheckedChange = { viewModel.onToggleTaken(item, it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = OnBox,
                                checkmarkColor = BoxColor,
                                uncheckedColor = OnBox
                            )
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                item.medicationName,
                                style = TextStyle(
                                    color = OnBox,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (item.taken) TextDecoration.LineThrough else TextDecoration.None
                                )
                            )
                            if (item.petName.isNotBlank()) {
                                Text(item.petName, style = TextStyle(color = Color.LightGray, fontSize = 14.sp))
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    ClockIcon,
                                    contentDescription = "Alarme definido",
                                    tint = OnBox,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "${item.timeLabel} todo dia",
                                    style = TextStyle(color = OnBox, fontSize = 14.sp)
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            TextButton(onClick = { viewModel.onEdit(item) }) { Text("Editar", color = OnBox) }
                            TextButton(onClick = { viewModel.onRequestDelete(item) }) {
                                Text("Excluir", color = Color(0xFFE57373))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = goHome,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BoxColor, contentColor = OnBox)
        ) {
            Text("Voltar", style = TextStyle(fontWeight = FontWeight.Bold))
        }
    }

    state.medicationPendingDelete?.let { medication ->
        AlertDialog(
            onDismissRequest = viewModel::onDismissDelete,
            containerColor = BoxColor,
            title = { Text("Excluir medicamento?", color = OnBox, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "${medication.medicationName} - ${medication.timeLabel}\nO alarme será cancelado.",
                    color = OnBox
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmDelete) { Text("Excluir", color = Color(0xFFE57373)) }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissDelete) { Text("Cancelar", color = OnBox) }
            }
        )
    }

    if (showTimePicker) {
        val timeState = rememberTimePickerState(initialHour = state.hour, initialMinute = state.minute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onTimeChange(timeState.hour, timeState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") } },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) { TimePicker(state = timeState) }
            }
        )
    }
}

@Composable
private fun FormField(value: String, label: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BoxColor,
            unfocusedBorderColor = BoxColor,
            focusedTextColor = OnBox,
            unfocusedTextColor = OnBox,
            cursorColor = OnBox,
            focusedLabelColor = Color.LightGray,
            unfocusedLabelColor = Color.LightGray,
            focusedContainerColor = BoxColor,
            unfocusedContainerColor = BoxColor
        ),
        singleLine = true
    )
}

@Composable
private fun schedulingViewModelFactory(): ViewModelProvider.Factory {
    val container = (LocalContext.current.applicationContext as PetLifeApp).container
    return viewModelFactory {
        initializer {
            SchedulingViewModel(
                observeMedications = container.observeMedications,
                addMedication = container.addMedication,
                updateMedication = container.updateMedication,
                removeMedication = container.removeMedication,
                setMedicationTaken = container.setMedicationTaken,
                canScheduleExactAlarms = container.canScheduleExactAlarms
            )
        }
    }
}
