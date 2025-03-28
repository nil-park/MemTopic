package com.nolbee.memtopic.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nolbee.memtopic.ui.theme.MemTopicTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsViewTopAppBar(
    vm: ISettingsViewModel = hiltViewModel<SettingsViewModel>()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "플레이 설정",  // TODO: replace this string with a string resource to achieve multi-language support.
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        },
        content = { innerPadding ->
            Column(
                Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!vm.isInitialized) {
                    return@Column
                }
                Text("플레이 속도: ${vm.settings.playbackSpeed}") // TODO: replace this string with a string resource to achieve multi-language support.
                Slider(
                    value = vm.settings.playbackSpeed,
                    onValueChange = { vm.updateSettings(vm.settings.copy(playbackSpeed = it)) },
                    valueRange = 0.5f..2.0f,
                    steps = 5
                )
                Text("문장 반복 회수: ${vm.settings.sentenceReputation}") // TODO: replace this string with a string resource to achieve multi-language support.
                Row(
                    Modifier.selectableGroup(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text("1회")
                    RadioButton(selected = vm.settings.sentenceReputation == 1, onClick = {
                        vm.updateSettings(vm.settings.copy(sentenceReputation = 1))
                    })
                    Text("2회")
                    RadioButton(selected = vm.settings.sentenceReputation == 2, onClick = {
                        vm.updateSettings(vm.settings.copy(sentenceReputation = 2))
                    })
                    Text("3회")
                    RadioButton(selected = vm.settings.sentenceReputation == 3, onClick = {
                        vm.updateSettings(vm.settings.copy(sentenceReputation = 3))
                    })
                    Text("5회")
                    RadioButton(selected = vm.settings.sentenceReputation == 5, onClick = {
                        vm.updateSettings(vm.settings.copy(sentenceReputation = 5))
                    })
                    Text("계속")
                    RadioButton(
                        selected = vm.settings.sentenceReputation == Int.MAX_VALUE,
                        onClick = {
                            vm.updateSettings(vm.settings.copy(sentenceReputation = Int.MAX_VALUE))
                        })
                }
                Text("발화 전 대기 시간 비율: ${vm.settings.preIntervalMultiplier}") // TODO: replace this string with a string resource to achieve multi-language support.)
                Slider(
                    value = vm.settings.preIntervalMultiplier,
                    onValueChange = { vm.updateSettings(vm.settings.copy(preIntervalMultiplier = it)) },
                    valueRange = 0.0f..3.0f,
                    steps = 5
                )
                Text("발화 후 대기 시간 비율: ${vm.settings.postIntervalMultiplier}") // TODO: replace this string with a string resource to achieve multi-language support.)
                Slider(
                    value = vm.settings.postIntervalMultiplier,
                    onValueChange = { vm.updateSettings(vm.settings.copy(postIntervalMultiplier = it)) },
                    valueRange = 0.0f..3.0f,
                    steps = 5
                )
            }
        }
    )
}

@Preview
@Composable
fun SettingsViewTopAppBarPreview() {
    MemTopicTheme {
        SettingsViewTopAppBar(vm = MockSettingsViewModel())
    }
}
