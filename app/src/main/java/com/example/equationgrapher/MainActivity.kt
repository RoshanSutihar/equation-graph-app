package com.example.equationgrapher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.equationgrapher.ui.theme.EquationGrapherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EquationGrapherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GraphingApp(innerPadding)
                }
            }
        }
    }
}
@Composable
fun GraphingApp(innerPadding: PaddingValues) {
    var equation by remember { mutableStateOf("") }
    var equations by remember { mutableStateOf(listOf<String>()) }
    var zoomLevel by remember { mutableStateOf(1f) }

    Row(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Graph view on the left (2/3 of the width)
        GraphCanvas(equations, Modifier.weight(2f), zoomLevel)

        Spacer(modifier = Modifier.width(16.dp))


        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {

            TextField(
                value = equation,
                onValueChange = { equation = it },
                label = { Text("Enter equation") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (equation.isNotEmpty()) {
                        equations = equations + equation
                        equation = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Plot")
            }

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "Equations",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color.LightGray.copy(alpha = 0.1f))
                    .padding(8.dp)
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            ) {
                equations.forEach { eq ->
                    Text(
                        text = eq,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Zoom slider
            Text("Zoom")
            Slider(
                value = zoomLevel,
                onValueChange = { zoomLevel = it },
                valueRange = 0.5f..2f,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = { equations = listOf() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Over")
            }
        }
    }
}

@Composable
fun GraphCanvas(equations: List<String>, modifier: Modifier = Modifier, zoomLevel: Float) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawGraph(equations, zoomLevel)
    }
}

fun DrawScope.drawGraph(equations: List<String>, zoomLevel: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2


    val scaleX = zoomLevel
    val scaleY = zoomLevel


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


    equations.forEachIndexed { index, equation ->
        val color = when (index % 3) {
            0 -> Color.Blue
            1 -> Color.Red
            else -> Color.Green
        }
        val path = createGraphPath(equation, scaleX, scaleY)
        drawPath(path, color, style = Stroke(width = 2f))
    }
}

fun createGraphPath(equation: String, scaleX: Float, scaleY: Float): androidx.compose.ui.graphics.Path {
    val path = androidx.compose.ui.graphics.Path()

    val parser = Parser(str = equation)
    val root = parser.parse()

    val xRange = -10f..10f
    val yRange = -10f..10f

    val canvasWidth = 200f
    val canvasHeight = 200f

    val step = 0.1f

    var firstPoint = true

    for (x in xRange.step(step)) {
        val y = root?.evalAt(x.toFloat())


        val scaledX = x * scaleX
        val scaledY = (y ?: 0f) * scaleY


        val canvasX = canvasWidth * (scaledX - xRange.start) / (xRange.endInclusive - xRange.start)
        val canvasY = canvasHeight * (yRange.endInclusive - scaledY) / (yRange.endInclusive - yRange.start)

        if (firstPoint) {
            path.moveTo(canvasX, canvasY)
            firstPoint = false
        } else {
            path.lineTo(canvasX, canvasY)
        }
    }
    return path
}

private fun ClosedFloatingPointRange<Float>.step(step: Float) =
    generateSequence(start) { it + step }
        .takeWhile { it <= endInclusive }
        .toList()
