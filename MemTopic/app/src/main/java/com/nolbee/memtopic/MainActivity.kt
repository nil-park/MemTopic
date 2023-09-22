package com.nolbee.memtopic

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nolbee.memtopic.ui.theme.MemTopicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemTopicTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TopicListLayout()
                }
            }
        }
    }
}

private const val TAG = "MainActivity"

@Composable
fun TopicListLayout() {
    Column(
        Modifier
            .padding(20.dp)
            .fillMaxSize(),
        Arrangement.SpaceBetween,
        Alignment.Start,
    ) {
        TitleText("토픽 목록") // TODO: replace this string with a string resource to achieve multi-language support.
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1.0f)
        ) {
            TopicTableView()
        }
        Row(
            Modifier
                .fillMaxWidth(),
            Arrangement.End,
            Alignment.CenterVertically
        ) {
            AddTopicIcon()
            Spacer(modifier = Modifier.width(15.dp))
            SettingsIcon()
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


@Composable
fun AddTopicIcon() {
    Icon(
        Icons.Rounded.AddCircle,
        "Add a Topic", // TODO: replace this string with a string resource to achieve multi-language support.
        Modifier
            .width(50.dp)
            .height(50.dp),
    )
}

@Composable
fun SettingsIcon() {
    var enabled by remember { mutableStateOf(true) }
    IconButton(
        {
            Log.d(TAG, "세팅 버튼을 클릭 하셨습니다.")
            enabled = false
        },
        enabled = enabled
    ) {
        Icon(
            Icons.Rounded.Settings,
            "Setting", // TODO: replace this string with a string resource to achieve multi-language support.
            Modifier
                .width(50.dp)
                .height(50.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopicListPreview() {
    MemTopicTheme {
        TopicListLayout()
    }
}
