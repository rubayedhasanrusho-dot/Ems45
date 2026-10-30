package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PerformanceStatus
import com.example.data.model.WeeklyScoreData
import com.example.ui.theme.EduRankAccent
import com.example.ui.theme.EduRankError
import com.example.ui.theme.EduRankPurple
import com.example.ui.theme.EduRankPurpleLight
import com.example.ui.theme.EduRankSuccess

@Composable
fun WeeklyPerformanceBarChart(
    weeklyData: List<WeeklyScoreData>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(top = 8.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val totalBars = weeklyData.size
                if (totalBars == 0) return@Canvas

                val availableWidth = size.width
                val availableHeight = size.height - 20.dp.toPx()
                val barWidth = (availableWidth / totalBars) * 0.45f
                val spacing = availableWidth / totalBars

                // Draw background guidelines
                val lineSteps = 3
                for (i in 0..lineSteps) {
                    val y = availableHeight * (i.toFloat() / lineSteps)
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(0f, y),
                        end = Offset(availableWidth, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw bars
                weeklyData.forEachIndexed { index, data ->
                    val x = (index * spacing) + (spacing - barWidth) / 2
                    val barHeight = (data.scorePercentage / 100f) * availableHeight
                    val y = availableHeight - barHeight

                    // Subtle background bar track
                    drawRoundRect(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        topLeft = Offset(x, 0f),
                        size = Size(barWidth, availableHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )

                    // Active score gradient bar
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(EduRankPurpleLight, EduRankPurple)
                        ),
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                    )
                }
            }
        }

        // Day labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            weeklyData.forEach { data ->
                Text(
                    text = data.dayName,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun AccuracyDonutChart(
    correct: Int,
    wrong: Int,
    unattempted: Int,
    modifier: Modifier = Modifier
) {
    val total = (correct + wrong + unattempted).coerceAtLeast(1).toFloat()
    val correctSweep = (correct / total) * 360f
    val wrongSweep = (wrong / total) * 360f
    val unattemptedSweep = (unattempted / total) * 360f

    val accuracy = if ((correct + wrong) > 0) {
        ((correct.toFloat() / (correct + wrong)) * 100).toInt()
    } else 0

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(100.dp)) {
                val strokeWidth = 12.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(radius * 2, radius * 2)

                // Track
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                // Correct
                drawArc(
                    color = EduRankSuccess,
                    startAngle = -90f,
                    sweepAngle = correctSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Wrong
                drawArc(
                    color = EduRankError,
                    startAngle = -90f + correctSweep,
                    sweepAngle = wrongSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$accuracy%",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Accuracy",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            LegendRow(color = EduRankSuccess, label = "Correct", value = "$correct questions")
            Spacer(modifier = Modifier.height(6.dp))
            LegendRow(color = EduRankError, label = "Wrong", value = "$wrong questions")
            Spacer(modifier = Modifier.height(6.dp))
            LegendRow(color = Color.Gray, label = "Unanswered", value = "$unattempted questions")
        }
    }
}

@Composable
private fun LegendRow(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = value,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun PerformanceStatusBadge(
    status: PerformanceStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, icon) = when (status) {
        PerformanceStatus.EXCELLENT -> Color(0xFFD1FAE5) to Icons.Default.Star
        PerformanceStatus.IMPROVING -> Color(0xFFEDE9FE) to Icons.Default.AutoGraph
        PerformanceStatus.AVERAGE -> Color(0xFFFEF3C7) to Icons.Default.CheckCircle
        PerformanceStatus.NEEDS_IMPROVEMENT -> Color(0xFFFEE2E2) to Icons.Default.Warning
    }

    val textColor = when (status) {
        PerformanceStatus.EXCELLENT -> Color(0xFF065F46)
        PerformanceStatus.IMPROVING -> EduRankPurple
        PerformanceStatus.AVERAGE -> Color(0xFF92400E)
        PerformanceStatus.NEEDS_IMPROVEMENT -> Color(0xFF991B1B)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = status.label,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = status.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun SubjectProgressBar(
    name: String,
    scorePercentage: Float,
    attemptsCount: Int,
    color: Color = EduRankPurple
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "${scorePercentage.toInt()}% ($attemptsCount tests)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { scorePercentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}
