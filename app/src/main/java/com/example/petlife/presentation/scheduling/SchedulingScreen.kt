package com.example.petlife.presentation.scheduling

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.petlife.R
import com.example.petlife.presentation.components.BackButton
import com.example.petlife.presentation.components.PetBlack
import com.example.petlife.presentation.components.PetField
import com.example.petlife.presentation.components.PetGray
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
private val BoxColor = PetBlack
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

    Column(modifier = Modifier.fillMaxSize().background(ScreenBackground).statusBarsPadding()) {
        BackButton(goHome, Modifier.padding(start = 12.dp, top = 8.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(23.dp))
                .background(PetGray)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Agende um horario do medicamento",
                style = TextStyle(color = Color.Black, fontSize = 22.sp),
                modifier = Modifier.fillMaxWidth()
            )
            if (!state.exactAlarmAllowed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Text(
                    "Sem permissão de alarme exato: o aviso pode atrasar. Toque para permitir.",
                    style = TextStyle(color = Color(0xFFB00020), fontSize = 14.sp),
                    modifier = Modifier.clickable {
                        context.startActivity(
                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                .setData(android.net.Uri.parse("package:${context.packageName}"))
                        )
                    }
                )
            }
            Text("Medicamento", style = TextStyle(color = Color.Black, fontSize = 22.sp))
            PetField(state.medicationName, viewModel::onMedicationNameChange)
            Text("Alarme", style = TextStyle(color = Color.Black, fontSize = 22.sp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Image(
                    painterResource(R.drawable.ic_clock_big),
                    contentDescription = "Definir alarme",
                    modifier = Modifier.size(79.dp).clip(RoundedCornerShape(13.dp)).clickable { showTimePicker = true }
                )
                Text(
                    "%02d:%02d".format(state.hour, state.minute),
                    style = TextStyle(color = Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.weight(1f))
                if (state.editingMedicationId != null) {
                    TextButton(onClick = viewModel::onCancelEdit) { Text("Cancelar", color = Color.Black) }
                }
                Button(
                    onClick = viewModel::onSchedule,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BoxColor, contentColor = OnBox)
                ) { Text(if (state.editingMedicationId != null) "Salvar" else "Agendar") }
            }
            state.errorMessage?.let { Text(it, style = TextStyle(color = Color(0xFFB00020), fontSize = 14.sp)) }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 47.dp, topEnd = 47.dp))
                .background(BoxColor),
            contentPadding = PaddingValues(start = 16.dp, top = 24.dp, end = 16.dp,
                bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.medications.isEmpty()) {
                item {
                    Text(
                        "Nenhum medicamento agendado",
                        style = TextStyle(color = Color.White, fontSize = 18.sp),
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(state.medications, key = { it.id }) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { viewModel.onEdit(item) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.medicationName,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            color = Color.Black,
                            fontSize = 22.sp,
                            textDecoration = if (item.taken) TextDecoration.LineThrough else TextDecoration.None
                        )
                    )
                    Text(item.timeLabel, style = TextStyle(color = Color.Black, fontSize = 22.sp))
                    Image(
                        painterResource(R.drawable.ic_check),
                        contentDescription = "Marcar como tomado",
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .alpha(if (item.taken) 1f else 0.3f)
                            .clickable { viewModel.onToggleTaken(item, !item.taken) }
                    )
                    Image(
                        painterResource(R.drawable.ic_trash),
                        contentDescription = "Excluir",
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(44.dp)
                            .clickable { viewModel.onRequestDelete(item) }
                    )
                }
            }
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
