package com.smartwake.alarm.ui.missions

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartwake.alarm.core.designsystem.BackgroundDark
import com.smartwake.alarm.core.designsystem.SurfaceVariantDark
import com.smartwake.alarm.core.designsystem.TextPrimary
import com.smartwake.alarm.core.designsystem.TextSecondary
import com.smartwake.alarm.platform.sensor.ShakeDetector

@Composable
fun ShakeMissionScreen(
    targetCount: Int = 40,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentShakes by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        val detector = ShakeDetector(
            context = context,
            targetShakes = targetCount,
            onProgress = { current, _ -> currentShakes = current },
            onComplete = onSuccess
        )
        detector.start()
        onDispose {
            detector.stop()
        }
    }

    val progress = (currentShakes.toFloat() / targetCount).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📳",
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "ЭНЕРГИЧНО ТРЯСИТЕ ТЕЛЕФОН",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Нужно сделать $targetCount встряхиваний для отключения",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Число сделанных встряхиваний
            Text(
                text = "$currentShakes / $targetCount",
                color = Color(0xFF38BDF8),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = Color(0xFF38BDF8),
                trackColor = SurfaceVariantDark,
            )
        }
    }
}
