package com.smartwake.alarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.smartwake.alarm.core.designsystem.CardBorder
import com.smartwake.alarm.core.designsystem.CrispEmerald
import com.smartwake.alarm.core.designsystem.SignalOrange
import com.smartwake.alarm.core.designsystem.SurfaceDark
import com.smartwake.alarm.core.designsystem.SurfaceVariantDark
import com.smartwake.alarm.core.designsystem.TextMuted
import com.smartwake.alarm.core.designsystem.TextPrimary
import com.smartwake.alarm.core.designsystem.TextSecondary
import com.smartwake.alarm.core.util.DateTimeUtils
import com.smartwake.alarm.domain.model.Alarm
import com.smartwake.alarm.domain.model.MissionType

@Composable
fun AlarmCard(
    alarm: Alarm,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val alpha = if (alarm.isEnabled) 1.0f else 0.45f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Время будильника (Display Mono)
                Text(
                    text = DateTimeUtils.formatTime(alarm.hour, alarm.minute),
                    color = TextPrimary.copy(alpha = alpha),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 42.sp,
                    letterSpacing = (-1).sp
                )

                // Тумблер переключения
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { newState ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onToggle(newState)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SignalOrange,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceVariantDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Дни недели и метка
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = DateTimeUtils.formatDaysOfWeek(alarm.daysOfWeekMask),
                        color = TextSecondary.copy(alpha = alpha),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (alarm.label.isNotBlank()) {
                        Text(
                            text = alarm.label,
                            color = TextMuted.copy(alpha = alpha),
                            fontSize = 13.sp
                        )
                    }
                }

                // Бейдж миссии
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MissionBadge(alarm.missionType, alpha)

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDelete()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Удалить",
                            tint = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MissionBadge(missionType: MissionType, alpha: Float = 1.0f) {
    val (label, icon, color) = when (missionType) {
        MissionType.QR_CODE -> Triple("QR-КОД", Icons.Outlined.QrCodeScanner, CrispEmerald)
        MissionType.MATH -> Triple("СЧЕТ", Icons.Outlined.Calculate, SignalOrange)
        MissionType.SHAKE -> Triple("ШЕЙК", Icons.Outlined.Vibration, Color(0xFF38BDF8))
        else -> Triple("ОБЫЧНЫЙ", null, TextMuted)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f * alpha))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color.copy(alpha = alpha),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }
            Text(
                text = label,
                color = color.copy(alpha = alpha),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}
