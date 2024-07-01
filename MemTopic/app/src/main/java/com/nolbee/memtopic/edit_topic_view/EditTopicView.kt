package com.nolbee.memtopic.edit_topic_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nolbee.memtopic.database.ITopicViewModel
import com.nolbee.memtopic.database.MockTopicViewModel
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.dialog_view.AlertAndConfirmView
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopicViewTopAppBar(
    navController: NavHostController,
    topicViewModel: ITopicViewModel,
    editTopicViewModel: EditTopicViewModel,
) {
    AlertAndConfirmView(
        if (editTopicViewModel.isNew) "Do you really want to add a new topic \"${editTopicViewModel.topicTitle}\"?" else "Do you really want to edit topic \"${editTopicViewModel.topicTitle}\"?",  // TODO: line is too long and the text is not from string resources
        editTopicViewModel,
        onConfirm = {
            topicViewModel.upsertTopic(
                Topic(
                    id = editTopicViewModel.topicRef.id,
                    title = editTopicViewModel.topicTitle,
                    content = editTopicViewModel.topicContent,
                    lastModified = Date(),
                    lastPlayback = editTopicViewModel.topicRef.lastPlayback,
                )
            )
            navController.navigateUp()
        }
    )
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
                    if (editTopicViewModel.isModified) {
                        IconButton(
                            onClick = {
                                editTopicViewModel.openAlertAndConfirmDialog = true
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
        visualTransformation = VisualTransformation.None,
        trailingIcon = {
            if (editTopicViewModel.isNew) {
                // TODO: 제목의 유일성을 검사해서 아이콘을 바꿔주기
//                Icon(imageVector = Icons.Filled.Warning , contentDescription = null, tint = Color.Red)
            }
        },
    )
}

@Composable
private fun TopicContentTextField(editTopicViewModel: EditTopicViewModel) {
    val scrollState = rememberScrollState()
    TextField(
        value = editTopicViewModel.topicContent,
        onValueChange = { editTopicViewModel.updateContent(it) },
        modifier = Modifier
            .fillMaxSize(1f)
            .verticalScroll(scrollState),
        singleLine = false,
        label = { Text("내용") },  // TODO: replace this string with a string resource to achieve multi-language support.
        visualTransformation = VisualTransformation.None,
    )
}

@Preview(showBackground = true)
@Composable
fun EditTopicPreview() {
    MemTopicTheme {
        EditTopicViewTopAppBar(
            navController = rememberNavController(),
            topicViewModel = MockTopicViewModel(),
            editTopicViewModel = EditTopicViewModel()
        )
    }
}
