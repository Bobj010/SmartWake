package com.smartwake.alarm.ui.missions

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.smartwake.alarm.core.designsystem.BackgroundDark
import com.smartwake.alarm.core.designsystem.CrispEmerald
import com.smartwake.alarm.core.designsystem.CriticalRed
import com.smartwake.alarm.core.designsystem.TextMuted
import com.smartwake.alarm.core.designsystem.TextPrimary
import com.smartwake.alarm.core.designsystem.TextSecondary
import com.smartwake.alarm.platform.camera.QrCodeAnalyzer
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(
    targetCode: String,
    onSuccess: () -> Unit,
    onEmergencyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current
    var isFlashOn by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Камера CameraX Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                val cameraExecutor = Executors.newSingleThreadExecutor()

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(
                                cameraExecutor,
                                QrCodeAnalyzer { scannedCode ->
                                    if (scannedCode.trim() == targetCode.trim() || targetCode.isBlank()) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onSuccess()
                                    } else {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        errorMessage = "Код не совпадает! Нужен код из ванной"
                                    }
                                }
                            )
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer
                        )
                        cameraControl = camera.cameraControl
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Полупрозрачный оверлей с зеленой рамкой видоискателя
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        // Центрированная рамка сканера
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "НАВЕДИТЕ КАМЕРУ НА КОД",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Зеленый прицел сканирования
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.5.dp, CrispEmerald, RoundedCornerShape(24.dp))
                    .background(Color.Transparent)
            )

            Spacer(modifier = Modifier.height(20.dp))

            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = CriticalRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            } ?: Text(
                text = "Отсканируйте эталонный код, чтобы выключить сигнал",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // Нижняя панель управления: Фонарик и Экстренный выход
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    isFlashOn = !isFlashOn
                    cameraControl?.enableTorch(isFlashOn)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = "Фонарик",
                    tint = if (isFlashOn) CrispEmerald else TextMuted,
                    modifier = Modifier.size(32.dp)
                )
            }

            TextButton(onClick = onEmergencyClick) {
                Text(
                    text = "Экстренный PIN-код",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}
