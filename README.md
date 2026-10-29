# Pet Life
<div align="center" >
    <img width="300" height="200" alt="image 5" src="https://github.com/user-attachments/assets/82a83e90-20e2-41be-8b0b-14f96345bbf5" />
    </div>

 <strong> Aplicativo Android nativo para agendamento de medicações pets </strong >

## Stack

<div align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-BOM_2026.02.01-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Material-3-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3" />
  <img src="https://img.shields.io/badge/Navigation_Compose-2.9.8-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Navigation Compose" />
  <img src="https://img.shields.io/badge/Room-2.8.5_(SQLite)-003B57?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room" />
  <img src="https://img.shields.io/badge/Coroutines_%26_Flow-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Coroutines e Flow" />
  <img src="https://img.shields.io/badge/MVVM-Clean_Architecture-FF6F00?style=for-the-badge&logo=android&logoColor=white" alt="MVVM" />
  <img src="https://img.shields.io/badge/AlarmManager-Notifications-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="AlarmManager" />
  <img src="https://img.shields.io/badge/Gradle-AGP_9.2.1-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle" />
  <img src="https://img.shields.io/badge/Testes-JUnit_4_%26_Espresso-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="Testes" />
  <img src="https://img.shields.io/badge/Android_Studio-JDK_21-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white" alt="Android Studio" />
</div>

## Arquitetura

- **presentation:** composables sem regra de negócio. `SchedulingViewModel` expõe um único `StateFlow<SchedulingUiState>` e recebe eventos por métodos (`onSchedule`, `onToggleTaken`, `onRequestDelete`…). Estado exclusivamente visual (ex.: diálogo do relógio) fica no composable.
- **domain:** não depende de Android. Define o modelo `Medication`, as interfaces consumidas pelos use cases e os use cases (`AddMedication`, `UpdateMedication`, `RemoveMedication`, `SetMedicationTaken`, `ObserveMedications`, `TriggerReminder`, `RescheduleAll`, `CanScheduleExactAlarms`). Validação e orquestração (salvar → agendar alarme → notificar) vivem em `AddMedicationUseCase`.
- **data:** `MedicationEntity`, `MedicationDao` e `PetLifeDatabase` (Room); `MedicationRepositoryImpl` converte entidade ↔ modelo de domínio.
- **alarm:** adaptadores de plataforma para as interfaces do domínio: `AlarmReminderScheduler` (`AlarmManager`) e `SystemMedicationNotifier` (`NotificationManager`), além dos `BroadcastReceiver`s.
- **di:** `AppContainer` (injeção manual, instanciado em `PetLifeApp`) é o único ponto que conhece as implementações concretas.

### Modelo de dados

Tabela `medications` (Room, versão 1, `exportSchema = false`):

| Coluna | Tipo | Observação |
|---|---|---|
| `id` | INTEGER PK | autoGenerate; também é o `requestCode` do `PendingIntent` |
| `petName` | TEXT | opcional (pode ser vazio) |
| `medicationName` | TEXT | obrigatório |
| `hour`, `minute` | INTEGER | horário diário do alarme |
| `taken` | INTEGER (bool) | dose do dia marcada pelo usuário |

### Estrutura

```
app/src/main/java/com/example/petlife/
├── PetLifeApp.kt                    # Application: cria o AppContainer e o canal de notificação
├── MainActivity.kt                  # NavHost (Splash → Home → Scheduling)
├── di/AppContainer.kt
├── domain/
│   ├── model/Medication.kt
│   ├── repository/MedicationRepository.kt
│   ├── scheduler/ReminderScheduler.kt
│   ├── notification/MedicationNotifier.kt
│   └── usecase/                     # Add, Update, Remove, SetTaken, Observe, TriggerReminder, RescheduleAll, CanScheduleExact
├── data/
│   ├── local/                       # MedicationEntity, MedicationDao, PetLifeDatabase
│   └── repository/MedicationRepositoryImpl.kt
├── alarm/                           # AlarmReminderScheduler, SystemMedicationNotifier, MedicationReceiver, BootReceiver
└── presentation/
    ├── splash/ home/                # SplashScreen, HomeScreen
    ├── scheduling/                  # SchedulingScreen, SchedulingViewModel, SchedulingUiState
    └── theme/                       # Color, Type, Theme
app/src/test/                        # testes unitários dos use cases (fakes das interfaces do domínio)
```

### Navegação

| Rota | Composable | Comportamento |
|---|---|---|
| `Splash` (start) | `SplashScreen` | Fade-in de 1200 ms e `delay(2500)`; vai para `Home` removendo a splash do back stack |
| `Home` | `HomeScreen` | Entrada principal |
| `Scheduling` | `SchedulingScreen` | Cadastro, edição, listagem, marcação de "tomou" e exclusão (com confirmação); a seta e o botão "Voltar" retornam à `Home` |

### Manifest

- Permissões: `POST_NOTIFICATIONS` (solicitada em runtime na tela de agendamento), `SCHEDULE_EXACT_ALARM`, `RECEIVE_BOOT_COMPLETED`
- `PetLifeApp` como `Application`; `MedicationReceiver` (`exported=false`); `BootReceiver` (`exported=true`, apenas ações do sistema)
- Sem permissão de alarme exato (Android 12+), o app usa `setAndAllowWhileIdle` (pode atrasar alguns minutos) e exibe aviso com atalho para a configuração.


### Passo a passo do agendamento

1. A tela envia `onSchedule()` ao `SchedulingViewModel`.
2. `AddMedicationUseCase` valida (nome não vazio, horário 00:00–23:59), grava no Room, agenda o alarme e dispara a notificação "Você tem um medicamento agendado".
3. No horário, o `AlarmManager` aciona `MedicationReceiver`, que (via `goAsync` + coroutine) executa `TriggerReminderUseCase`: desmarca o "já tomou", exibe a notificação do lembrete e agenda o disparo do dia seguinte (alarmes exatos não se repetem).
4. **Alteração:** "Editar" no box do medicamento carrega nome, pet e horário no formulário (`onEdit`); "Salvar" aciona `UpdateMedicationUseCase`, que valida, atualiza a linha no Room, desmarca o "já tomou" (nova dose) e reagenda o alarme com o mesmo id (o `PendingIntent` com o mesmo `requestCode` substitui o disparo anterior, sem duplicar). "Cancelar edição" descarta o formulário; excluir o item em edição também o limpa.
5. `BootReceiver` reagenda tudo após reboot, atualização do app e mudança de hora/fuso/permissão de alarme exato.

## Build

```bash
./gradlew assembleDebug               # APK: app/build/outputs/apk/debug/
./gradlew installDebug                # instala no dispositivo/emulador via adb
./gradlew testDebugUnitTest           # testes unitários (JVM)
./gradlew connectedDebugAndroidTest   # testes instrumentados
./gradlew lintDebug                   # análise estática
```
Requisitos: Android SDK 36 e JDK 21 (o Gradle resolve a toolchain via Foojay), ou Android Studio compatível com AGP 9.x.
