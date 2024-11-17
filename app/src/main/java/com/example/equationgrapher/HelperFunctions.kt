package com.example.equationgrapher

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.pow

fun DrawScope.drawGraph(
    equations: List<String>,
    zoomLevel: Float,
    xOffset: Float,
    yOffset: Float
) {
    // Capture the canvas size in variables
    val canvasWidth = size.width
    val canvasHeight = size.height

    // Calculate center and scale based on the size
    val centerX = canvasWidth / 2
    val centerY = canvasHeight / 2

    // Constrain zoom level to avoid extreme values
    val constrainedZoom = zoomLevel.coerceIn(0.5f, 2f) // Prevent zoom level from being too small or too large
    val scaleX = constrainedZoom * 100f
    val scaleY = constrainedZoom * 100f

    // Constrain offsets
    val constrainedXOffset = constrainOffset(xOffset, constrainedZoom, canvasWidth, true)
    val constrainedYOffset = constrainOffset(yOffset, constrainedZoom, canvasHeight, false)

    // Draw the X and Y axes
    drawLine(
        color = Color.Black,
        start = Offset(centerX + constrainedXOffset, 0f),
        end = Offset(centerX + constrainedXOffset, canvasHeight),
        strokeWidth = 2f
    )
    drawLine(
        color = Color.Black,
        start = Offset(0f, centerY + constrainedYOffset),
        end = Offset(canvasWidth, centerY + constrainedYOffset),
        strokeWidth = 2f
    )

    // Draw axis labels
    drawAxisLabels(centerX + constrainedXOffset, centerY + constrainedYOffset, scaleX, scaleY, xOffset, yOffset, canvasWidth, canvasHeight)

    // Draw equations
    equations.forEachIndexed { index, equation ->
        val color = when (index % 3) {
            0 -> Color.Blue
            1 -> Color.Red
            else -> Color.Green
        }
        val path = createGraphPath(
            equation = equation,
            scaleX = scaleX,
            scaleY = scaleY,
            centerX = centerX + constrainedXOffset,
            centerY = centerY + constrainedYOffset
        )
        drawPath(path, color, style = Stroke(width = 6f))
    }
}

fun constrainOffset(
    offset: Float,
    zoomLevel: Float,
    canvasSize: Float,
    isXAxis: Boolean = true
): Float {
    val maxOffset = maxOf(1f, (canvasSize / 2) * (zoomLevel - 1))
    return offset.coerceIn(-maxOffset, maxOffset)
}

fun DrawScope.drawAxisLabels(
    centerX: Float,
    centerY: Float,
    scaleX: Float,
    scaleY: Float,
    xOffset: Float,
    yOffset: Float,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 30f
        textAlign = android.graphics.Paint.Align.CENTER
    }

    // Calculate the visible X range based on offset and zoom
    val visibleXStart = ((-centerX + xOffset) / scaleX).toInt() - 1
    val visibleXEnd = ((canvasWidth - centerX + xOffset) / scaleX).toInt() + 1

    // Draw X-axis labels
    for (i in visibleXStart..visibleXEnd) {
        val x = centerX + i * scaleX
        drawContext.canvas.nativeCanvas.drawText(
            i.toString(),
            x,
            centerY + 40f, // Adjusted for better placement
            paint
        )
    }

    // Calculate the visible Y range based on offset and zoom
    val visibleYStart = ((-centerY + yOffset) / scaleY).toInt() - 1
    val visibleYEnd = ((canvasHeight - centerY + yOffset) / scaleY).toInt() + 1

    // Draw Y-axis labels
    for (i in visibleYStart..visibleYEnd) {
        val y = centerY - i * scaleY
        drawContext.canvas.nativeCanvas.drawText(
            i.toString(),
            centerX + 40f,
            y,
            paint
        )
    }
}

fun createGraphPath(equation: String, scaleX: Float, scaleY: Float, centerX: Float, centerY: Float): Path {
    val path = Path()
    val xRange = -10f..10f
    val step = 0.1f
    var firstPoint = true

    generateSequence(xRange.start) { it + step }
        .takeWhile { it <= xRange.endInclusive }
        .forEach { x ->
            val y = evaluateEquation(equation, x)
            val scaledX = x * scaleX
            val scaledY = y * scaleY
            val canvasX = centerX + scaledX
            val canvasY = centerY - scaledY

            if (firstPoint) {
                path.moveTo(canvasX, canvasY)
                firstPoint = false
            } else {
                path.lineTo(canvasX, canvasY)
            }
        }

    return path
}

fun evaluateEquation(equation: String, x: Float): Float {
    return try {
        val terms = equation.split("+").map { it.trim() }

        terms.map { term ->
            when {
                term.contains("x^2") -> {
                    val coefficient = term.replace("x^2", "").toFloatOrNull() ?: 1f
                    coefficient * x.pow(2)
                }
                term.contains("x") -> {
                    val coefficient = term.replace("x", "").toFloatOrNull() ?: 1f
                    coefficient * x
                }
                else -> term.toFloatOrNull() ?: 0f
            }
        }.sum()
    } catch (e: Exception) {
        0f
    }
}
