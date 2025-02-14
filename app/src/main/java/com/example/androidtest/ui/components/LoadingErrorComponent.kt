package com.example.androidtest.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidtest.ui.theme.loadingBarBackgroundColor
import com.example.androidtest.ui.theme.loadingBarColor
import com.example.androidtest.viewmodel.OpenChargeMapViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoadingErrorComponent() {
    val viewModel: OpenChargeMapViewModel = koinViewModel()
    val loadingUIState = viewModel.loadingUIState.collectAsStateWithLifecycle().value
    val infiniteTransition = rememberInfiniteTransition()

    val screenWidth = viewModel.screenWidth.collectAsStateWithLifecycle().value

    // Animate the position of the moving color segment
    val offsetX = infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = screenWidth.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animate size of the moving piece
    val size = infiniteTransition.animateFloat(
        initialValue = 50f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (loadingUIState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color(loadingBarBackgroundColor))
            ) {
                Box(
                    modifier = Modifier
                        .size(size.value.dp, 15.dp) // Size of the moving piece
                        .offset { IntOffset(offsetX.value.toInt(), 0) } // Move it horizontally
                        .background(Color(loadingBarColor))
                )
            }
        } else if (loadingUIState.error != null) {
            Column(modifier = Modifier.fillMaxWidth().background(Color(0xAA000000)).align(Alignment.BottomCenter), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                    text = loadingUIState.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium)
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp).clickable(onClick = {
                        viewModel.retry()
                    }),
                    text = "Retry",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Cyan)
            }
        }
    }
}