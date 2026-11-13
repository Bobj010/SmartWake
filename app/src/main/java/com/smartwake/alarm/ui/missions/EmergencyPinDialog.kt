package com.smartwake.alarm.ui.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartwake.alarm.core.designsystem.CardBorder
import com.smartwake.alarm.core.designsystem.CriticalRed
import com.smartwake.alarm.core.designsystem.SignalOrange
import com.smartwake.alarm.core.designsystem.SurfaceDark
import com.smartwake.alarm.core.designsystem.TextMuted
import com.smartwake.alarm.core.designsystem.TextPrimary
import com.smartwake.alarm.core.designsystem.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyPinDialog(
    onDismiss: () -> Unit,
    onConfirmPin: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = "ЭКСТРЕННОЕ ОТКЛЮЧЕНИЕ",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Если QR-код утерян или поврежден, введите 6-значный аварийный PIN-код (по умолчанию 123456):",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        pin = it
                        isError = false
                    },
                    singleLine = true,
                    isError = isError,
                    label = { Text("PIN-код", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SignalOrange,
                        unfocusedBorderColor = CardBorder,
                        errorBorderColor = CriticalRed,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                if (isError) {
                    Text(
                        text = "Неверный PIN-код",
                        color = CriticalRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (pin.trim().length >= 4) {
                        onConfirmPin(pin.trim())
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Отключить", color = SignalOrange, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = TextMuted)
            }
        }
    )
}
