package com.nolbee.memtopic.edit_topic_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nolbee.memtopic.R
import com.nolbee.memtopic.database.ITopicViewModel
import com.nolbee.memtopic.database.MockTopicViewModel
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopicViewTopAppBar(
    navController: NavHostController,
    topicViewModel: ITopicViewModel,
    editTopicViewModel: EditTopicViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (editTopicViewModel.isNew) "새 토픽" else "토픽 편집",  // TODO: replace this string with a string resource to achieve multi-language support.
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    TopicSelectVoiceButton(editTopicViewModel)
                    if (editTopicViewModel.isSavable) {
                        IconButton(
                            onClick = {
                                editTopicViewModel.isConfirmDialogOpen = true
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                            )
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TopicTitleTextField(editTopicViewModel)
                TopicContentTextField(editTopicViewModel)
            }
        }
    )

    val confirmMessage = if (editTopicViewModel.isNew) {
        stringResource(R.string.add_new_topic_confirm, editTopicViewModel.topicTitle)
    } else {
        stringResource(R.string.edit_topic_confirm, editTopicViewModel.topicTitle)
    }

    ConfirmView(
        content = confirmMessage,
        vm = editTopicViewModel,
        onConfirm = {
            topicViewModel.upsertTopic(
                Topic(
                    id = editTopicViewModel.topicRef.id,
                    title = editTopicViewModel.topicTitle,
                    content = editTopicViewModel.topicContent,
                    options = editTopicViewModel.encodeVoiceOptionsToJson(),
                    lastModified = Date(),
                    lastPlayback = editTopicViewModel.topicRef.lastPlayback,
                )
            )
            navController.navigateUp()
        }
    )

    if (editTopicViewModel.openBottomSheet) {
        EditTopicSettingsViewGCP(vm = editTopicViewModel)
    }
}

@Composable
private fun TopicTitleTextField(editTopicViewModel: EditTopicViewModel) {
    TextField(
        value = editTopicViewModel.topicTitle,
        onValueChange = { editTopicViewModel.updateTitle(it) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = !editTopicViewModel.isNew,
        singleLine = true,
        label = { Text("제목") },  // TODO: replace this string with a string resource to achieve multi-language support.
    )
}

@Composable
private fun TopicContentTextField(editTopicViewModel: EditTopicViewModel) {
    TextField(
        value = editTopicViewModel.topicContent,
        onValueChange = { editTopicViewModel.updateContent(it) },
        modifier = Modifier.fillMaxSize(),
        maxLines = Int.MAX_VALUE,
        label = { Text("내용") },  // TODO: replace this string with a string resource to achieve multi-language support.
    )
}

@Composable
private fun TopicSelectVoiceButton(vm: EditTopicViewModel) {
    TextButton(
        onClick = {
            vm.openBottomSheet = true
        }
    ) {
        Column {
            Text("음성 코드")
            Text(vm.selectedVoiceType)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditTopicPreview() {
    MemTopicTheme {
        EditTopicViewTopAppBar(
            navController = rememberNavController(),
            topicViewModel = MockTopicViewModel(),
            editTopicViewModel = EditTopicViewModel()
        )
    }
}
