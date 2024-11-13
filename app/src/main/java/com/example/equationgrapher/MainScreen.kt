package com.example.equationgrapher

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .clipToBounds()
        ) {
            GraphCanvas(equations, Modifier.fillMaxSize(), zoomLevel)
        }

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
    Canvas(modifier = modifier) {
        drawGraph(equations, zoomLevel)
    }
}
