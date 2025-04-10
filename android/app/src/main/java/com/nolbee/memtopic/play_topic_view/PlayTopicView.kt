package com.nolbee.memtopic.play_topic_view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nolbee.memtopic.database.sampleTopic00
import com.nolbee.memtopic.ui.theme.MemTopicTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayTopicViewTopAppBar(
    vm: IPlayTopicViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Play Topic", // TODO: use string resource
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp
                        )
                        Text(
                            text = vm.topicToPlay.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    PlayerSettingsButton(vm)
                }
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

    if (vm.openBottomSheet) {
        PlayerSettingsView(playTopicViewModel = vm)
    }
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

@Composable
private fun PlayerSettingsButton(vm: IPlayTopicViewModel) {
    TextButton(
        onClick = {
            vm.openBottomSheet = true
        }
    ) {
        Text("재생 설정")
    }
}
