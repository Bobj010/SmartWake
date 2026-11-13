package com.smartwake.alarm.ui.missions

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartwake.alarm.core.designsystem.BackgroundDark
import com.smartwake.alarm.core.designsystem.CardBorder
import com.smartwake.alarm.core.designsystem.CriticalRed
import com.smartwake.alarm.core.designsystem.SignalOrange
import com.smartwake.alarm.core.designsystem.SurfaceDark
import com.smartwake.alarm.core.designsystem.SurfaceVariantDark
import com.smartwake.alarm.core.designsystem.TextMuted
import com.smartwake.alarm.core.designsystem.TextPrimary
import com.smartwake.alarm.core.designsystem.TextSecondary
import com.smartwake.alarm.domain.model.MathDifficulty
import com.smartwake.alarm.domain.usecase.MathGenerator
import com.smartwake.alarm.domain.usecase.MathProblem

@Composable
fun MathMissionScreen(
    difficulty: MathDifficulty,
    questionsCount: Int = 3,
    onSuccess: () -> Unit,
    onEmergencyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var currentStep by remember { mutableIntStateOf(1) }
    var currentProblem by remember { mutableStateOf(MathGenerator.generateProblem(difficulty)) }
    var inputAnswer by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Заголовок и прогресс
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "КОГНИТИВНЫЙ РАЗГОН",
                color = SignalOrange,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Задача $currentStep из $questionsCount",
                color = TextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Математическое выражение крупным шрифтом
            Text(
                text = currentProblem.expression,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 44.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Поле введенного ответа
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(
                        1.5.dp,
                        if (hasError) CriticalRed else CardBorder,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inputAnswer.ifBlank { "?" },
                    color = if (hasError) CriticalRed else TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
        }

        // Калькуляторная клавиатура (Thumb Zone)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val buttons = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "OK")
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(64.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (key == "OK") SignalOrange else SurfaceVariantDark)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    when (key) {
                                        "C" -> {
                                            inputAnswer = ""
                                            hasError = false
                                        }
                                        "OK" -> {
                                            if (MathGenerator.checkAnswer(currentProblem, inputAnswer)) {
                                                hasError = false
                                                inputAnswer = ""
                                                if (currentStep >= questionsCount) {
                                                    onSuccess()
                                                } else {
                                                    currentStep++
                                                    currentProblem = MathGenerator.generateProblem(difficulty)
                                                }
                                            } else {
                                                hasError = true
                                                inputAnswer = ""
                                            }
                                        }
                                        else -> {
                                            if (inputAnswer.length < 5) {
                                                inputAnswer += key
                                                hasError = false
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                color = if (key == "OK") BackgroundDark else TextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            TextButton(onClick = onEmergencyClick) {
                Text(
                    text = "Экстренный PIN-код",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
