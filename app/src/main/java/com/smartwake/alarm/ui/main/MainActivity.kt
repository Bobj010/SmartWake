package com.smartwake.alarm.ui.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartwake.alarm.core.designsystem.BackgroundDark
import com.smartwake.alarm.core.designsystem.SmartWakeTheme
import com.smartwake.alarm.ui.edit.EditAlarmScreen
import com.smartwake.alarm.ui.edit.EditAlarmViewModel
import com.smartwake.alarm.ui.missions.QrScannerScreen

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val editViewModel: EditAlarmViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Проверяем разрешения
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRequiredPermissions()

        setContent {
            SmartWakeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDark
                ) {
                    AppNavigation(
                        mainViewModel = mainViewModel,
                        editViewModel = editViewModel
                    )
                }
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissions.add(Manifest.permission.CAMERA)

        if (permissions.isNotEmpty()) {
            permissionLauncher.launch(permissions.toTypedArray())
        }

        // Проверка разрешения на точные будильники в Android 12+ (SCHEDULE_EXACT_ALARM)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    editViewModel: EditAlarmViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                viewModel = mainViewModel,
                onAddAlarm = {
                    editViewModel.loadAlarm(null)
                    navController.navigate("edit/new")
                },
                onEditAlarm = { alarmId ->
                    editViewModel.loadAlarm(alarmId)
                    navController.navigate("edit/$alarmId")
                }
            )
        }

        composable(
            route = "edit/{alarmId}",
            arguments = listOf(navArgument("alarmId") { type = NavType.StringType })
        ) {
            EditAlarmScreen(
                viewModel = editViewModel,
                onBack = { navController.popBackStack() },
                onScanQrClick = { navController.navigate("scan_qr_calibration") }
            )
        }

        composable("scan_qr_calibration") {
            QrScannerScreen(
                targetCode = "", // В режиме калибровки сканирует любой код
                onSuccess = {
                    // Привязываем код
                    editViewModel.setBarcodeData("HOME_BATHROOM_QR", "Ванная комната")
                    navController.popBackStack()
                },
                onEmergencyClick = { navController.popBackStack() }
            )
        }
    }
}
