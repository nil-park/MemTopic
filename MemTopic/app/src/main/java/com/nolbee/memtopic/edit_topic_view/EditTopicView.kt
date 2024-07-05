package com.nolbee.memtopic.edit_topic_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
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
private fun EditTopicPreview() {
    MemTopicTheme {
        EditTopicViewTopAppBar(
            navController = rememberNavController(),
            topicViewModel = MockTopicViewModel(),
            editTopicViewModel = EditTopicViewModel()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmView(
    content: String,
    vm: EditTopicViewModel,
    onConfirm: () -> Unit,
) {
    if (vm.isConfirmDialogOpen) {
        BasicAlertDialog(
            onDismissRequest = {
                vm.isConfirmDialogOpen = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = content,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = {
                            onConfirm()
                            vm.isConfirmDialogOpen = false
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmViewPreview() {
    MemTopicTheme {
        val vm = EditTopicViewModel()
        vm.isConfirmDialogOpen = true
        ConfirmView(
            content = "Do you really want to do so?",
            vm = vm,
            onConfirm = {}
        )
    }
}
