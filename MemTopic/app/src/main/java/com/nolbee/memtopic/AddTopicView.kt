package com.nolbee.memtopic

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nolbee.memtopic.ui.theme.MemTopicTheme

private const val TAG = "AddTopicView"

@Composable
fun AddTopicView() {
    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxSize(),
    ) {
        TitleText(text = "New Topic")
        Spacer(modifier = Modifier.height(10.dp))
        TopicNameView(
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        TopicContentView(
            modifier = Modifier.fillMaxWidth()
                .weight(1.0f, true)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            DiscardButton()
            Spacer(modifier = Modifier.width(15.dp))
            ApplyButton()
        }
    }
}

@Composable
private fun TitleText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicNameView(modifier: Modifier) {
    var topicName by remember { mutableStateOf(TextFieldValue()) }
    OutlinedTextField(
        value = topicName,
        onValueChange = { topicName = it },
        label = { Text("The unique topic name") },
        singleLine = true,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopicContentView(modifier: Modifier) {
    var topicContent by remember { mutableStateOf(TextFieldValue()) }
    OutlinedTextField(
        value = topicContent,
        onValueChange = { topicContent = it },
        label = { Text("Input your topic content here.\n\nPlease add line breaks to separate blocks.") },
        singleLine = false,
        modifier = modifier
    )
}

@Composable
private fun ApplyButton() {
    Icon(
        Icons.Rounded.CheckCircle,
        "Apply", // TODO: replace this string with a string resource to achieve multi-language support.
        Modifier
            .width(50.dp)
            .height(50.dp)
            .clickable {
                Log.d(TAG, "You clicked apply icon.")
            }
    )
}

@Composable
private fun DiscardButton() {
    Icon(
        Icons.Rounded.Delete,
        "Discard", // TODO: replace this string with a string resource to achieve multi-language support.
        Modifier
            .width(50.dp)
            .height(50.dp)
            .clickable {
                Log.d(TAG, "You clicked back icon.")
            }
    )
}


@Preview(showBackground = true)
@Composable
fun AddTopicPreview() {
    MemTopicTheme {
        AddTopicView()
    }
}
