package com.smartwake.alarm.ui.ringing

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartwake.alarm.core.designsystem.BackgroundDark
import com.smartwake.alarm.core.designsystem.CrispEmerald
import com.smartwake.alarm.core.designsystem.SignalOrange
import com.smartwake.alarm.core.designsystem.TextMuted
import com.smartwake.alarm.core.designsystem.TextPrimary
import com.smartwake.alarm.core.designsystem.TextSecondary
import com.smartwake.alarm.core.util.DateTimeUtils
import com.smartwake.alarm.domain.model.MissionType
import com.smartwake.alarm.ui.components.PrimaryButton
import com.smartwake.alarm.ui.missions.EmergencyPinDialog
import com.smartwake.alarm.ui.missions.MathMissionScreen
import com.smartwake.alarm.ui.missions.QrScannerScreen
import com.smartwake.alarm.ui.missions.ShakeMissionScreen
import java.util.Calendar

@Composable
fun AlarmRingScreen(
    viewModel: AlarmRingViewModel,
    onDismissAlarm: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val alarm by viewModel.alarm.collectAsState()
    val mission by viewModel.mission.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    var showPinDialog by remember { mutableStateOf(false) }

    when (uiState) {
        is RingUiState.Ringing -> {
            // Главный экран звонящего будильника
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAlpha"
            )

            val currentCal = Calendar.getInstance()
            val timeString = DateTimeUtils.formatTime(
                currentCal.get(Calendar.HOUR_OF_DAY),
                currentCal.get(Calendar.MINUTE)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Пульсирующий индикатор тревоги
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(SignalOrange)
                            .alpha(pulseAlpha)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "СИГНАЛ БУДИЛЬНИКА",
                        color = SignalOrange,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Огромные моноширинные часы
                    Text(
                        text = timeString,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 72.sp,
                        letterSpacing = (-2).sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = alarm?.label?.ifBlank { "Подъем" } ?: "Подъем",
                        color = TextSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(60.dp))

                    // Кнопка начала миссии
                    val missionTitle = when (alarm?.missionType) {
                        MissionType.QR_CODE -> "СКАНИРОВАТЬ QR-КОД"
                        MissionType.MATH -> "РЕШИТЬ ЗАДАЧИ"
                        MissionType.SHAKE -> "ТРЯСТИ ТЕЛЕФОН"
                        else -> "ОТКЛЮЧИТЬ СИГНАЛ"
                    }

                    PrimaryButton(
                        text = missionTitle,
                        onClick = { viewModel.startMission() },
                        backgroundColor = SignalOrange,
                        contentColor = BackgroundDark
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Кнопка аварийного PIN-кода (Safe-Exit)
                    TextButton(onClick = { showPinDialog = true }) {
                        Text(
                            text = "Экстренное отключение (PIN)",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }

                    Text(
                        text = "Звонок длится: ${elapsedSeconds}с (авто-стоп через 300с)",
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        }

        is RingUiState.MissionScreen -> {
            // Экран активной миссии в зависимости от типа
            when (alarm?.missionType) {
                MissionType.QR_CODE -> {
                    QrScannerScreen(
                        targetCode = mission?.targetBarcodeHash ?: "",
                        onSuccess = { viewModel.onMissionCompleted(wasSuccess = true) },
                        onEmergencyClick = { showPinDialog = true }
                    )
                }
                MissionType.MATH -> {
                    MathMissionScreen(
                        difficulty = mission?.mathDifficulty ?: com.smartwake.alarm.domain.model.MathDifficulty.MEDIUM,
                        questionsCount = mission?.mathQuestionsCount ?: 3,
                        onSuccess = { viewModel.onMissionCompleted(wasSuccess = true) },
                        onEmergencyClick = { showPinDialog = true }
                    )
                }
                MissionType.SHAKE -> {
                    ShakeMissionScreen(
                        targetCount = mission?.shakeTargetCount ?: 40,
                        onSuccess = { viewModel.onMissionCompleted(wasSuccess = true) }
                    )
                }
                else -> {
                    viewModel.onMissionCompleted(wasSuccess = true)
                }
            }
        }

        is RingUiState.Success -> {
            // Финальный победный экран
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundDark)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CrispEmerald.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = CrispEmerald,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "ВЫ ПРОСНУЛИСЬ!",
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Миссия успешно пройдена за $elapsedSeconds сек.",
                        color = TextSecondary,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    PrimaryButton(
                        text = "Хорошего дня",
                        onClick = onDismissAlarm,
                        backgroundColor = CrispEmerald,
                        contentColor = BackgroundDark
                    )
                }
            }
        }
    }

    if (showPinDialog) {
        EmergencyPinDialog(
            onDismiss = { showPinDialog = false },
            onConfirmPin = { pin ->
                val success = viewModel.unlockViaEmergencyPin(pin)
                if (success) {
                    showPinDialog = false
                }
            }
        )
    }
}
