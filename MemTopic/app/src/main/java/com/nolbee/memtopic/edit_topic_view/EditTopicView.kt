package com.nolbee.memtopic.edit_topic_view

import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nolbee.memtopic.database.ITopicViewModel
import com.nolbee.memtopic.database.MockTopicViewModel
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.database.TopicViewModel
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import java.util.Date

private const val TAG = "AddTopicView"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopicViewTopAppBar(
    navController: NavHostController = rememberNavController(),
    topicViewModel: ITopicViewModel = hiltViewModel<TopicViewModel>(),
    editTopicViewModel: EditTopicViewModel = EditTopicViewModel(),
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "토픽 편집",  // TODO: replace this string with a string resource to achieve multi-language support.
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    if (editTopicViewModel.isModified) {
                        IconButton(
                            onClick = {
                                Log.d(
                                    TAG,
                                    "You clicked add topic: ${editTopicViewModel.topicTitle}"
                                )
                                topicViewModel.addTopic(
                                    Topic(
                                        title = editTopicViewModel.topicTitle,
                                        content = editTopicViewModel.topicContent,
                                        lastModified = Date(),
                                        lastPlayback = Date(),
                                    )
                                )
                                navController.popBackStack()
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
private fun TopicTitleTextField(viewModel: EditTopicViewModel) {
    TextField(
        value = viewModel.topicTitle,
        onValueChange = { viewModel.updateTitle(it) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = !viewModel.isNew,
        singleLine = true,
        label = { Text("제목") },  // TODO: replace this string with a string resource to achieve multi-language support.
        visualTransformation = VisualTransformation.None,
        trailingIcon = {
            if (viewModel.isNew) {
                // TODO: 제목의 유일성을 검사해서 아이콘을 바꿔주기
//                Icon(imageVector = Icons.Filled.Warning , contentDescription = null, tint = Color.Red)
            }
        },
    )
}

@Composable
private fun TopicContentTextField(viewModel: EditTopicViewModel) {
    val scrollState = rememberScrollState()
    TextField(
        value = viewModel.topicContent,
        onValueChange = { viewModel.updateContent(it) },
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
            topicViewModel = MockTopicViewModel()
        )
    }
}
