package com.nolbee.memtopic.play_topic_view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.nolbee.memtopic.R
import com.nolbee.memtopic.database.sampleTopic00
import com.nolbee.memtopic.ui.theme.MemTopicTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayTopicViewTopAppBar(
    vm: IPlayTopicViewModel,
) {
    val playTopicTitle = stringResource(R.string.play_topic_title, vm.topicToPlay.title)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = playTopicTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                HorizontalDivider()
                PlayableLineList(vm)
            }
        }
    )
}


@Preview
@Composable
fun PlayTopicViewTopAppBarPreview() {
    val vm = MockPlayTopicViewModel()
    vm.setTopic(sampleTopic00)
    MemTopicTheme {
        PlayTopicViewTopAppBar(
            vm = vm
        )
    }
}
