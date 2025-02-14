package com.example.androidtest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidtest.getPlugImageFromId
import com.example.androidtest.viewmodel.OpenChargeMapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewStationComponent(viewModel: OpenChargeMapViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val selectedStation = viewModel.selectedStation.collectAsStateWithLifecycle()

    if(selectedStation.value != null) {
        ModalBottomSheet(
            shape = RoundedCornerShape(0.dp),
            onDismissRequest = {
                viewModel.selectedStation.value = null
            },
            sheetState = sheetState,
        ) {
            val station = selectedStation.value
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                Text(station?.addressInfo?.title ?: "",
                    style = MaterialTheme.typography.bodyLarge)
                Text(station?.addressInfo?.addressLine1 ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))

                LazyRow(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(station?.connections?.size ?: 0) { index ->
                        val connection = station?.connections?.get(index)
                        val quantity = connection?.quantity
                        val powerKW = connection?.powerKW ?: "?"

                        Card {
                            Row(modifier = Modifier.background(station?.statusType?.color() ?: MaterialTheme.colorScheme.primary).padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    modifier = Modifier.size(40.dp),
                                    painter = painterResource(id = getPlugImageFromId(connection?.connectionType?.id)),
                                    contentDescription = "Plug icon")

                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.Start) {
                                    Text(powerKW.toString().plus(" kW"))
                                    if(quantity != null)
                                        Text(quantity.toString().plus("x"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}