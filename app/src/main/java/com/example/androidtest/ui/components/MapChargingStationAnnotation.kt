package com.example.androidtest.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
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
import com.example.androidtest.models.ChargingStation
import com.example.androidtest.viewmodel.OpenChargeMapViewModel

@Composable
fun MapChargingStationAnnotation(viewModel: OpenChargeMapViewModel, station: ChargingStation) {
    val backgroundColor = station.backgroundColor()
    val painter = if(station.statusType?.isOperational == true)
        painterResource(R.drawable.electric_bolt)
    else
        painterResource(R.drawable.electric_bolt_offline)
    Canvas(
        modifier = Modifier
            .clickable(onClick = {
                viewModel.selectedStation.value = station
            })
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
            color = backgroundColor,
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
            color = backgroundColor
        )

        // Draw the icon
        with(painter){
            translate(left = middlePoint - iconWidth / 2, top = (rectHeight - iconHeight) / 2) {
                draw(size = Size(iconWidth, iconHeight))
            }
        }
    }
}