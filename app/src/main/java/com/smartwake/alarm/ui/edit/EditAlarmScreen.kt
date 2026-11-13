package com.smartwake.alarm.ui.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartwake.alarm.core.designsystem.BackgroundDark
import com.smartwake.alarm.core.designsystem.CardBorder
import com.smartwake.alarm.core.designsystem.CrispEmerald
import com.smartwake.alarm.core.designsystem.SignalOrange
import com.smartwake.alarm.core.designsystem.SurfaceDark
import com.smartwake.alarm.core.designsystem.SurfaceVariantDark
import com.smartwake.alarm.core.designsystem.TextMuted
import com.smartwake.alarm.core.designsystem.TextPrimary
import com.smartwake.alarm.core.designsystem.TextSecondary
import com.smartwake.alarm.domain.model.MathDifficulty
import com.smartwake.alarm.domain.model.MissionType
import com.smartwake.alarm.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlarmScreen(
    viewModel: EditAlarmViewModel,
    onBack: () -> Unit,
    onScanQrClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val hour by viewModel.hour.collectAsState()
    val minute by viewModel.minute.collectAsState()
    val daysMask by viewModel.daysMask.collectAsState()
    val label by viewModel.label.collectAsState()
    val missionType by viewModel.missionType.collectAsState()
    val barcodeLabel by viewModel.barcodeLabel.collectAsState()
    val mathDifficulty by viewModel.mathDifficulty.collectAsState()

    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = TextPrimary
                    )
                }
                Text(
                    text = "НАСТРОЙКА СИГНАЛА",
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                PrimaryButton(
                    text = "Сохранить будильник",
                    onClick = {
                        viewModel.setTime(timePickerState.hour, timePickerState.minute)
                        viewModel.saveAlarm(onSaved = onBack)
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Пикер времени Material 3
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = SurfaceVariantDark,
                        clockDialSelectedContentColor = BackgroundDark,
                        clockDialUnselectedContentColor = TextPrimary,
                        selectorColor = SignalOrange,
                        periodSelectorBorderColor = CardBorder,
                        periodSelectorSelectedContainerColor = SignalOrange,
                        periodSelectorUnselectedContainerColor = SurfaceVariantDark,
                        periodSelectorSelectedContentColor = BackgroundDark,
                        periodSelectorUnselectedContentColor = TextPrimary,
                        timeSelectorSelectedContainerColor = SignalOrange,
                        timeSelectorUnselectedContainerColor = SurfaceVariantDark,
                        timeSelectorSelectedContentColor = BackgroundDark,
                        timeSelectorUnselectedContentColor = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Чипы дней недели
            Text(
                text = "ПОВТОРЕНИЕ",
                color = TextMuted,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf(
                    "Пн" to 1, "Вт" to 2, "Ср" to 4, "Чт" to 8, "Пт" to 16, "Сб" to 32, "Вс" to 64
                )
                days.forEach { (name, bit) ->
                    val isSelected = (daysMask and bit) != 0
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SignalOrange else SurfaceDark)
                            .border(1.dp, if (isSelected) SignalOrange else CardBorder, CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleDay(bit)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) BackgroundDark else TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Название будильника
            OutlinedTextField(
                value = label,
                onValueChange = { viewModel.setLabel(it) },
                label = { Text("Название будильника", color = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SignalOrange,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Секция выбора миссии
            Text(
                text = "СПОСОБ ОТКЛЮЧЕНИЯ (МИССИЯ)",
                color = TextMuted,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            MissionOptionCard(
                title = "QR / Штрих-код",
                subtitle = "Физический подъем: скан кода в ванной ($barcodeLabel)",
                isSelected = missionType == MissionType.QR_CODE,
                color = CrispEmerald,
                onClick = { viewModel.setMissionType(MissionType.QR_CODE) }
            )

            if (missionType == MissionType.QR_CODE) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceVariantDark)
                        .clickable { onScanQrClick() }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = CrispEmerald
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Привязать код камерой прямо сейчас",
                            color = CrispEmerald,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            MissionOptionCard(
                title = "Математический счет",
                subtitle = "Когнитивный разгон: решение примеров на логику",
                isSelected = missionType == MissionType.MATH,
                color = SignalOrange,
                onClick = { viewModel.setMissionType(MissionType.MATH) }
            )

            if (missionType == MissionType.MATH) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MathDifficulty.values().forEach { diff ->
                        val isDiffSelected = mathDifficulty == diff
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDiffSelected) SignalOrange else SurfaceVariantDark)
                                .clickable { viewModel.setMathDifficulty(diff) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (diff) {
                                    MathDifficulty.EASY -> "Легко"
                                    MathDifficulty.MEDIUM -> "Средне"
                                    MathDifficulty.HARD -> "Сложно"
                                },
                                color = if (isDiffSelected) BackgroundDark else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            MissionOptionCard(
                title = "Встряхивание (Shake)",
                subtitle = "Разгон пульса: 40 энергичных встряхиваний смартфона",
                isSelected = missionType == MissionType.SHAKE,
                color = Color(0xFF38BDF8),
                onClick = { viewModel.setMissionType(MissionType.SHAKE) }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MissionOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(
                1.5.dp,
                if (isSelected) color else CardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = if (isSelected) color else TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}
