package com.example.androidtest.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.androidtest.R
import com.example.androidtest.api.models.ChargingStation

@Composable
fun MapChargingStationAnnotation(station: ChargingStation) {
    val color = station.statusType?.color() ?: return
    // Return if statusType is not available, I don't want to display incomplete charging station

    val painter = painterResource(R.drawable.electric_bolt)
    Canvas(
        modifier = Modifier
            .height(40.dp) // Increased height to accommodate the triangle
            .width(35.dp)
    ) {
        val rectHeight = size.height - 10.dp.toPx()
        val rectWidth = size.width
        val middlePoint = size.width / 2
        val iconHeight = rectHeight - 10.dp.toPx()
        val iconWidth = rectWidth - 10.dp.toPx()

        // Draw the rectangle
        drawRoundRect(
            color = color,
            size = Size(rectWidth, rectHeight),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
        )

        // Draw the triangle
        drawPath(
            path = Path().apply {
                moveTo(middlePoint, rectHeight + 10.dp.toPx()) // Bottom middle (Tip of the triangle)
                lineTo(middlePoint - 5.dp.toPx(), rectHeight - 1.dp.toPx()) // Bottom-left corner of the rectangle
                lineTo(middlePoint + 5.dp.toPx(), rectHeight - 1.dp.toPx()) // Bottom-right corner of the rectangle
                close()
            },
            color = color
        )

        // Draw the icon
        with(painter){
            translate(left = middlePoint - iconWidth / 2, top = (rectHeight - iconHeight) / 2) {
                draw(size = Size(iconWidth, iconHeight))
            }
        }
    }
}