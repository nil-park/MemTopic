package com.nolbee.memtopic.topic_list_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun TopicListTopAppBar(
    navController: NavHostController,
    topicViewModel: ITopicViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "토픽 리스트",  // TODO: replace this string with a string resource to achieve multi-language support.
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = {
                        topicViewModel.topicToEdit = Topic(
                            title = "", content = "", lastModified = Date(), lastPlayback = Date()
                        )
                        navController.navigate("EditTopicView")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            TopicList(
                navController = navController,
                topicViewModel = topicViewModel,
                innerPadding = innerPadding,
            )
        }
    )

    val deleteConfirmMessage =
        stringResource(R.string.delete_topic_confirm, topicViewModel.topicToDelete.title)

    DeleteConfirmView(
        content = deleteConfirmMessage,
        vm = topicViewModel,
        onConfirm = {
            topicViewModel.deleteTopic()
        },
        onDismiss = {
            topicViewModel.topicToDelete = Topic()
        },
    )
}

@Composable
private fun TopicList(
    navController: NavHostController,
    topicViewModel: ITopicViewModel,
    innerPadding: PaddingValues
) {
    val topics by topicViewModel.topics.collectAsState(initial = emptyList())

    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(topics) { topic ->
            TopicListItem(
                topic = topic,
                navController = navController,
                topicViewModel = topicViewModel,
            )
        }
    }
}

@Preview
@Composable
fun TopicListTopAppBarPreview() {
    MemTopicTheme {
        TopicListTopAppBar(
            navController = rememberNavController(),
            topicViewModel = MockTopicViewModel()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteConfirmView(
    content: String,
    vm: ITopicViewModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (vm.isDeleteConfirmDialogOpen) {
        BasicAlertDialog(
            onDismissRequest = {
                onDismiss()
                vm.isDeleteConfirmDialogOpen = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation,
                color = MaterialTheme.colorScheme.error
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = Color(240, 195, 0, 255)
                    )
                    Text(
                        text = content,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = {
                            onConfirm()
                            vm.isDeleteConfirmDialogOpen = false
                        },
                        modifier = Modifier.align(Alignment.End),
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
private fun DeleteConfirmViewPreview() {
    MemTopicTheme {
        val vm = MockTopicViewModel()
        vm.isDeleteConfirmDialogOpen = true
        DeleteConfirmView(
            content = "Do you really want to do so?",
            vm = vm,
            onConfirm = {},
            onDismiss = {},
        )
    }
}
