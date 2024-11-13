package com.example.equationgrapher

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.pow


fun DrawScope.drawGraph(equations: List<String>, zoomLevel: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val scaleX = zoomLevel * 100f
    val scaleY = zoomLevel * 100f

    drawLine(
        color = Color.Black,
        start = Offset(centerX, 0f),
        end = Offset(centerX, size.height),
        strokeWidth = 2f
    )
    drawLine(
        color = Color.Black,
        start = Offset(0f, centerY),
        end = Offset(size.width, centerY),
        strokeWidth = 2f
    )

    drawAxisLabels(centerX, centerY, scaleX, scaleY)

    equations.forEachIndexed { index, equation ->
        val color = when (index % 3) {
            0 -> Color.Blue
            1 -> Color.Red
            else -> Color.Green
        }
        val path = createGraphPath(equation, scaleX, scaleY, centerX, centerY)
        drawPath(path, color, style = Stroke(width = 2f))
    }
}

fun DrawScope.drawAxisLabels(centerX: Float, centerY: Float, scaleX: Float, scaleY: Float) {
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 30f
        textAlign = android.graphics.Paint.Align.CENTER
    }

    for (i in -12..12 step 1) {
        val x = centerX + i * scaleX
        drawContext.canvas.nativeCanvas.drawText(
            i.toString(), x, centerY + 20, paint
        )
    }

    for (i in -10..10 step 1) {
        val y = centerY - i * scaleY
        drawContext.canvas.nativeCanvas.drawText(
            i.toString(), centerX + 20, y, paint
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
            val scaledY = y * scaleY // Scale the y-value according to the zoom level
            val canvasX = centerX + scaledX
            val canvasY = centerY - scaledY // Flip the y-values to align with the canvas (positive values should go up)

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
