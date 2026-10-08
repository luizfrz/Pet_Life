# Pet Life

Aplicativo Android nativo para agendamento de medicações veterinárias, escrito em Kotlin com Jetpack Compose.

## Especificações

| Item | Valor |
|---|---|
| Application ID / namespace | `com.example.petlife` |
| `minSdk` / `targetSdk` / `compileSdk` | 24 / 36 / 36 |
| Versão | `1.0` (`versionCode = 1`) |
| Linguagem | Kotlin 2.2.10 (plugin `kotlin.compose`) |
| Android Gradle Plugin | 9.2.1 |
| Toolchain JDK | 21 (`gradle-daemon-jvm.properties`) |
| Bytecode alvo | Java 11 |
| UI toolkit | Jetpack Compose, BOM `2026.02.01`, Material 3 |
| Navegação | `navigation-compose` 2.9.8 |
| Activity / Lifecycle | `activity-compose` 1.13.0 / `lifecycle-runtime-ktx` 2.10.0 |
| Testes | JUnit 4.13.2, AndroidX JUnit 1.3.0, Espresso 3.7.0, Compose UI Test |

Versões centralizadas em `gradle/libs.versions.toml` (version catalog). `isMinifyEnabled = false` no build `release`.

## Arquitetura

Single-activity (`MainActivity : ComponentActivity`), edge-to-edge habilitado, UI 100% Compose. O grafo de navegação é declarado em `MainActivity` por um `NavHost` com rotas string-based.

| Rota | Composable | Comportamento |
|---|---|---|
| `Splash` (start) | `SplashScreen` | Fade-in (`tween` 1200 ms) e `delay(2500)` em `LaunchedEffect`; navega para `Home` com `popUpTo("Splash") { inclusive = true }`, removendo a splash do back stack |
| `Home` | `HomeScreen` | Entrada principal; navega para `Scheduling` |
| `Scheduling` | `SchedulingScreen` | `OutlinedTextField` + botão de adição; lista mantida em `mutableStateListOf<String>()` |

### Estrutura de diretórios

```
.
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/petlife/
│       │   ├── MainActivity.kt          # NavHost / grafo de rotas
│       │   └── presentation/
│       │       ├── splash/              # SplashScreen
│       │       ├── home/                # HomeScreen
│       │       ├── scheduling/          # SchedulingScreen
│       │       └── theme/               # Color.kt, Type.kt, Theme.kt
│       └── res/                         # drawables, mipmaps, values, xml
├── gradle/
│   ├── libs.versions.toml               # version catalog
│   └── gradle-daemon-jvm.properties     # toolchain JDK 21
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

### Tema

`PetLIfeTheme` (Material 3) usa `dynamicColorScheme` em Android 12+ (API 31, `Build.VERSION_CODES.S`) e faz fallback para `lightColorScheme`/`darkColorScheme` estáticos conforme `isSystemInDarkTheme()`. As telas ainda definem a cor de marca `#2D6498` diretamente nos composables, sem passar pelo `ColorScheme`.

### Manifest

- Permissão: `POST_NOTIFICATIONS`
- Activity: `.MainActivity` (`exported=true`, `MAIN`/`LAUNCHER`)
- Receiver: `.alarm.MedicationReceiver` (`exported=false`), **classe inexistente** (suprimido com `tools:ignore="MissingClass"`)

## Ambiente de desenvolvimento

**Android Studio** é o ambiente primário. **VS Code** é suportado como ambiente secundário para edição de Kotlin/Compose.

### VS Code

- **Kotlin:** extensão oficial [`jetbrains.kotlin-server`](https://marketplace.visualstudio.com/items?itemName=jetbrains.kotlin-server) (Kotlin LSP, motor do IntelliJ). Importa o projeto via Gradle; o suporte a Android Gradle Plugin é **experimental** e a extensão está em alfa. Pode haver falsos positivos em símbolos gerados (`R`, Compose compiler plugin) que o Android Studio resolve.
- **Complementares:** `vscjava.vscode-gradle`, `redhat.vscode-xml` (resources/manifest), `tamasfe.even-better-toml` (`libs.versions.toml`), `editorconfig.editorconfig`. Lista em `.vscode/extensions.json`.
- **Formatação:** `.editorconfig` (Kotlin official style, 120 colunas), aplicada pelo formatter da JetBrains.
- **Tasks** (`.vscode/tasks.json`): `assembleDebug`, `installDebug`, `testDebugUnitTest`, `lint`, `clean`, `adb devices`, `logcat` filtrado por PID, launch da `MainActivity`.
- **SDK:** o Gradle lê `sdk.dir` de `local.properties` (não versionado):
  ```properties
  sdk.dir=/caminho/para/Android/Sdk
  ```
- **VS Code via Flatpak:** o sandbox não possui JDK nem SDK. O `.vscode/settings.json` (local, não versionado) define o terminal integrado como `flatpak-spawn --host bash`, de modo que Gradle e `adb` rodam no host.

## Build

Requisitos: Android SDK 36 e JDK 21 (o Gradle resolve a toolchain via Foojay), ou Android Studio compatível com AGP 9.x.

```bash
./gradlew assembleDebug          # APK: app/build/outputs/apk/debug/
./gradlew installDebug           # instala no dispositivo/emulador via adb
./gradlew testDebugUnitTest      # testes unitários (JVM)
./gradlew connectedDebugAndroidTest   # testes instrumentados
./gradlew lint                   # análise estática
```