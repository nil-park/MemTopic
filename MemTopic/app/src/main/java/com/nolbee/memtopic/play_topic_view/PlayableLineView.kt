package com.nolbee.memtopic.play_topic_view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.nolbee.memtopic.database.sampleTopic00
import com.nolbee.memtopic.ui.theme.MemTopicTheme


@Composable
fun PlayableLineList(
    vm: PlayTopicViewModel
) {
    val lines by vm.playableLines.collectAsState(initial = emptyList())

    LazyColumn {
        itemsIndexed(lines) { index, text ->
            PlayableLineItem(
                index = index,
                text = text,
                vm = vm,
            )
        }
    }
}

@Preview
@Composable
fun PlayableLineListPreview() {
    val vm = PlayTopicViewModel()
    vm.setTopic(sampleTopic00)
    MemTopicTheme {
        PlayableLineList(
            vm = vm
        )
    }
}

@Composable
fun PlayableLineItem(
    index: Int,
    text: String,
    vm: PlayTopicViewModel,
) {
    val currentIndex by vm.currentLineIndex.collectAsState()
    val isPlaying = currentIndex == index
    Column {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { vm.setCurrentLine(index) },
            headlineContent = {
                Text(
                    text = text,
                    fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal
                )
            },
            supportingContent = {
                if (isPlaying) {
                    Text(
                        text = "Playing...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.End)
                    )
                }
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Filled.ArrowCircleDown,
                    contentDescription = "Select line"
                )
            }
        )
        HorizontalDivider()
    }
}
