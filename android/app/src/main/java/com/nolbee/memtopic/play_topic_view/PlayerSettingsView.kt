package com.nolbee.memtopic.play_topic_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SheetValue.Expanded
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nolbee.memtopic.ui.theme.MemTopicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettingsView(
    playTopicViewModel: IPlayTopicViewModel,
    settingsViewModel: IPlayerSettingsViewModel = hiltViewModel<PlayerSettingsViewModel>()
) {
    val bottomSheetState = rememberSheetState(
        skipPartiallyExpanded = true,
        initialValue = if (settingsViewModel is MockPlayerSettingsViewModel) {
            Expanded
        } else Hidden
    )
    ModalBottomSheet(
        onDismissRequest = { playTopicViewModel.openBottomSheet = false },
        sheetState = bottomSheetState
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 15.dp,
                    top = 15.dp,
                    end = 15.dp,
                    bottom = 100.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!settingsViewModel.isInitialized) {
                return@Column
            }
            Text("플레이 속도: ${settingsViewModel.settings.playbackSpeed}") // TODO: replace this string with a string resource to achieve multi-language support.
            Slider(
                value = settingsViewModel.settings.playbackSpeed,
                onValueChange = {
                    settingsViewModel.updateSettings(
                        settingsViewModel.settings.copy(
                            playbackSpeed = it
                        )
                    )
                },
                valueRange = 0.5f..2.0f,
                steps = 5
            )
            val sentenceReputationOptions = listOf("1회", "2회", "3회", "5회", "계속")
            val sentenceReputationIndex =
                sentenceReputationToIndex(settingsViewModel.settings.sentenceReputation)
            Text("문장 반복 회수: ${sentenceReputationOptions[sentenceReputationIndex]}") // TODO: replace this string with a string resource to achieve multi-language support.
            SingleChoiceSegmentedButtonRow(
                Modifier.fillMaxWidth()
            ) {
                sentenceReputationOptions.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = sentenceReputationOptions.size
                        ),
                        onClick = {
                            settingsViewModel.updateSettings(
                                settingsViewModel.settings.copy(
                                    sentenceReputation = indexToSentenceReputation(index)
                                )
                            )
                        },
                        selected = index == sentenceReputationIndex
                    ) {
                        Text(label)
                    }
                }
            }
            Text("발화 전 대기 시간 비율: ${settingsViewModel.settings.preIntervalMultiplier}") // TODO: replace this string with a string resource to achieve multi-language support.)
            Slider(
                value = settingsViewModel.settings.preIntervalMultiplier,
                onValueChange = {
                    settingsViewModel.updateSettings(
                        settingsViewModel.settings.copy(
                            preIntervalMultiplier = it
                        )
                    )
                },
                valueRange = 0.0f..3.0f,
                steps = 5
            )
            Text("발화 후 대기 시간 비율: ${settingsViewModel.settings.postIntervalMultiplier}") // TODO: replace this string with a string resource to achieve multi-language support.)
            Slider(
                value = settingsViewModel.settings.postIntervalMultiplier,
                onValueChange = {
                    settingsViewModel.updateSettings(
                        settingsViewModel.settings.copy(
                            postIntervalMultiplier = it
                        )
                    )
                },
                valueRange = 0.0f..3.0f,
                steps = 5
            )
        }
    }
}

@Composable
@ExperimentalMaterial3Api
private fun rememberSheetState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
    initialValue: SheetValue = Hidden,
    skipHiddenState: Boolean = false,
): SheetState {
    val density = LocalDensity.current
    return rememberSaveable(
        skipPartiallyExpanded, confirmValueChange,
        saver = SheetState.Saver(
            skipPartiallyExpanded = skipPartiallyExpanded,
            confirmValueChange = confirmValueChange,
            density = density
        )
    ) {
        SheetState(
            skipPartiallyExpanded,
            density,
            initialValue,
            confirmValueChange,
            skipHiddenState
        )
    }
}

private fun sentenceReputationToIndex(count: Int) = when (count) {
    1 -> 0
    2 -> 1
    3 -> 2
    5 -> 3
    Int.MAX_VALUE -> 4
    else -> 0
}

private fun indexToSentenceReputation(index: Int) = when (index) {
    0 -> 1
    1 -> 2
    2 -> 3
    3 -> 5
    4 -> Int.MAX_VALUE
    else -> 1
}

@Preview
@Composable
fun SettingsViewTopAppBarPreview() {
    val playTopicViewModel = MockPlayTopicViewModel()
    val settingsViewModel = MockPlayerSettingsViewModel()
    playTopicViewModel.openBottomSheet = true
    MemTopicTheme {
        PlayerSettingsView(
            playTopicViewModel = playTopicViewModel,
            settingsViewModel = settingsViewModel,
        )
    }
}
