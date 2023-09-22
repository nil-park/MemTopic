package com.nolbee.memtopic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import java.util.Date

private const val TAG = "TopicTableView"

@Composable
fun TopicTableView() {
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
private fun getRandomString(length: Int): String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { charset.random() }
        .joinToString("")
}

@Preview(showBackground = true)
@Composable
fun TopicTablePreview() {
    MemTopicTheme {
        TopicTableView()
    }
}
