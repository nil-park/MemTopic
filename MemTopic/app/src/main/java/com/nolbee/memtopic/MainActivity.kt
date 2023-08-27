package com.nolbee.memtopic

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import java.util.Date

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

private const val TAG = "TopicListView"

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
            TableScreen()
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
fun TitleText(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
fun RowScope.TableHeadText(text: String, weight: Float) {
    Text(
        text,
        Modifier
            .weight(weight)
            .padding(8.dp),
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun TableHead(weights: FloatArray) {
    Row(Modifier.background(Color.Gray)) {
        TableHeadText("이름", weights[0])
        TableHeadText("날짜", weights[1])
        TableHeadText("길이", weights[2])
        TableHeadText("", weights[3])
    }
}

@Composable
fun RowScope.TableContentText(text: String, weight: Float) {
    Text(
        text,
        Modifier
            .weight(weight)
            .padding(8.dp)
    )
}

data class TopicAbstract(val name: String, val lastModified: Date, val duration: String)

@Composable
fun TableContent(weights: FloatArray, data: TopicAbstract) {
    Row(Modifier.background(Color.Gray)) {
        TableHeadText(data.name, weights[0])
        TableHeadText(data.lastModified.toString(), weights[1])
        TableHeadText(data.duration, weights[2])
        TableHeadText("", weights[3])
    }
}

// TODO: this is test purpose random generator. remove this in production.
fun getRandomString(length: Int): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { charset.random() }
        .joinToString("")
}

// TODO: move all of composable inside TableScreen UI to another file.
@Composable
fun TableScreen() {
    // Just a fake data... a Pair of Int and String
    val tableData = (1..100).map { index ->
        index to TopicAbstract(
            getRandomString(8),
            Date(),
            "3:30"
        )
    }
    // Each cell of a column must have the same weight.
    val weights = floatArrayOf(0.25f, 0.25f, 0.25f, 0.25f)
    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(
        Modifier
            .fillMaxSize()
    ) {
        // Here is the header
        item {
            TableHead(weights)
        }
        // Here are all the lines of your table.
        items(tableData) {
            val (_, data) = it
            TableContent(weights, data)
        }
    }
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