package com.example.ui.common

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.hypot

enum class PatternState {
    INPUT,
    SUCCESS,
    ERROR
}

@Composable
fun PatternLockView(
    modifier: Modifier = Modifier,
    patternState: PatternState = PatternState.INPUT,
    onPatternCompleted: (List<Int>) -> Unit,
    onPatternCleared: () -> Unit = {}
) {
    val context = LocalContext.current
    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    val selectedPoints = remember { mutableStateListOf<Int>() }
    var currentTouchPosition by remember { mutableStateOf<Offset?>(null) }

    val lineColor by animateColorAsState(
        targetValue = when (patternState) {
            PatternState.INPUT -> MaterialTheme.colorScheme.primary
            PatternState.SUCCESS -> MaterialTheme.colorScheme.tertiary
            PatternState.ERROR -> MaterialTheme.colorScheme.error
        },
        label = "lineColor"
    )

    fun performHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .size(300.dp)
            .aspectRatio(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(patternState) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            selectedPoints.clear()
                            onPatternCleared()
                            currentTouchPosition = offset
                            val point = getHitPoint(offset, size.width.toFloat(), size.height.toFloat())
                            if (point != null) {
                                selectedPoints.add(point)
                                performHaptic()
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val pos = change.position
                            currentTouchPosition = pos
                            val point = getHitPoint(pos, size.width.toFloat(), size.height.toFloat())
                            if (point != null && !selectedPoints.contains(point)) {
                                selectedPoints.add(point)
                                performHaptic()
                            }
                        },
                        onDragEnd = {
                            currentTouchPosition = null
                            if (selectedPoints.isNotEmpty()) {
                                onPatternCompleted(selectedPoints.toList())
                            }
                        },
                        onDragCancel = {
                            currentTouchPosition = null
                            selectedPoints.clear()
                            onPatternCleared()
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val cellWidth = width / 3f
            val cellHeight = height / 3f

            fun getCenterOfPoint(idx: Int): Offset {
                val row = idx / 3
                val col = idx % 3
                return Offset(
                    x = col * cellWidth + cellWidth / 2f,
                    y = row * cellHeight + cellHeight / 2f
                )
            }

            // Draw connecting lines between already visited dots
            if (selectedPoints.size > 1) {
                for (i in 0 until selectedPoints.size - 1) {
                    val p1 = getCenterOfPoint(selectedPoints[i])
                    val p2 = getCenterOfPoint(selectedPoints[i + 1])
                    drawLine(
                        color = lineColor,
                        start = p1,
                        end = p2,
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw line to current touch drag position
            if (selectedPoints.isNotEmpty() && currentTouchPosition != null && patternState == PatternState.INPUT) {
                val lastPointCenter = getCenterOfPoint(selectedPoints.last())
                drawLine(
                    color = lineColor.copy(alpha = 0.7f),
                    start = lastPointCenter,
                    end = currentTouchPosition!!,
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Draw 3x3 Nodes
            for (idx in 0 until 9) {
                val center = getCenterOfPoint(idx)
                val isSelected = selectedPoints.contains(idx)

                val outerRadius = 24.dp.toPx()
                val innerRadius = 7.dp.toPx()

                if (isSelected) {
                    // Outer glow circle
                    drawCircle(
                        color = lineColor.copy(alpha = 0.25f),
                        radius = outerRadius,
                        center = center
                    )
                    drawCircle(
                        color = lineColor,
                        radius = outerRadius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Inner filled dot
                    drawCircle(
                        color = lineColor,
                        radius = innerRadius * 1.3f,
                        center = center
                    )
                } else {
                    // Inactive dot
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = innerRadius,
                        center = center
                    )
                }
            }
        }
    }
}

private fun getHitPoint(offset: Offset, width: Float, height: Float): Int? {
    val cellWidth = width / 3f
    val cellHeight = height / 3f
    val hitRadius = cellWidth * 0.38f

    for (row in 0 until 3) {
        for (col in 0 until 3) {
            val centerX = col * cellWidth + cellWidth / 2f
            val centerY = row * cellHeight + cellHeight / 2f
            val dist = hypot(offset.x - centerX, offset.y - centerY)
            if (dist <= hitRadius) {
                return row * 3 + col
            }
        }
    }
    return null
}

fun List<Int>.toPatternString(): String = this.joinToString("-")
