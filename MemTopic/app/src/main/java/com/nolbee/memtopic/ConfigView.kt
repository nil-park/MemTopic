package com.nolbee.memtopic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nolbee.memtopic.ui.theme.MemTopicTheme

@Composable
fun ConfigView() {  // TODO: modularize sub components
    val textState = remember { mutableStateOf("") }
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
    ) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                "TTS 설정",  // TODO: replace this string with a string resource to achieve multi-language support.
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box (
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary),
            )
            {
                Column (
                    Modifier.padding(16.dp),
                ) {
                    Text(
                        text = "토큰",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextField(
                        value = textState.value,
                        onValueChange = { newText -> textState.value = newText },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("토큰 입력") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Text
                        ),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfigPreview() {
    MemTopicTheme {
        ConfigView()
    }
}
