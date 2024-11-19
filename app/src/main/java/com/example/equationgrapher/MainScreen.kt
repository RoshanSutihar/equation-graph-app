package com.example.equationgrapher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GraphingApp(innerPadding: PaddingValues) {
    var equation by remember { mutableStateOf("") }
    var equations by remember { mutableStateOf(listOf<String>()) }
    var zoomLevel by remember { mutableStateOf(1.2f) }

    var xOffset by remember { mutableStateOf(0f) }
    var yOffset by remember { mutableStateOf(0f) }

    Row(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .clipToBounds()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        xOffset += dragAmount.x
                        yOffset += dragAmount.y
                        change.consume()
                    }
                }
        ) {
            GraphCanvas(
                equations = equations,
                modifier = Modifier.fillMaxSize(),
                zoomLevel = zoomLevel,
                xOffset = xOffset,
                yOffset = yOffset
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(
                text = "Note: Write eqn as it is. Do not include * sign to multiply variabe and integer. Write '3x' not '3*x'.",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(16.dp)
            )


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

            Text("Zoom")
            Slider(
                value = zoomLevel,
                onValueChange = {

                    zoomLevel = it.coerceIn(1f, 2f)
                },
                valueRange = 1f..1.5f,
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
fun GraphCanvas(
    equations: List<String>,
    modifier: Modifier = Modifier,
    zoomLevel: Float,
    xOffset: Float,
    yOffset: Float
) {
    Canvas(modifier = modifier) {

        val canvasWidth = size.width
        val canvasHeight = size.height


        val constrainedXOffset = constrainOffset(xOffset, zoomLevel, canvasWidth, true)
        val constrainedYOffset = constrainOffset(yOffset, zoomLevel, canvasHeight, false)


        translate(left = constrainedXOffset, top = constrainedYOffset) {
            scale(zoomLevel, zoomLevel, pivot = Offset(canvasWidth / 2, canvasHeight / 2)) {
                drawGraph(equations, zoomLevel, constrainedXOffset, constrainedYOffset)
            }
        }
    }
}



