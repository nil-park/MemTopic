package com.nolbee.memtopic.topic_list_view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nolbee.memtopic.database.ITopicViewModel
import com.nolbee.memtopic.database.MockTopicViewModel
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.database.sampleTopic00
import com.nolbee.memtopic.ui.theme.MemTopicTheme

@Composable
fun TopicListItem(
    topic: Topic,
    navController: NavHostController,
    topicViewModel: ITopicViewModel,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column {
        ListItem(
            headlineContent = {
                Text(
                    topic.title,
                    modifier = Modifier.clickable {
                        topicViewModel.topicToPlay = topic
                        navController.navigate("PlayTopicView")
                    }
                )
            },
            supportingContent = {
                Text(
                    topic.content,
                    modifier = Modifier.clickable {
                        topicViewModel.topicToPlay = topic
                        navController.navigate("PlayTopicView")
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                Row {
                    IconButton(onClick = {
                        topicViewModel.topicToEdit = topic
                        navController.navigate("EditTopicView")
                    }) {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                    }
                    IconButton(onClick = {
                        topicViewModel.topicToDelete = topic
                        topicViewModel.isDeleteConfirmDialogOpen = true
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                    }
                }
            },
        )
        HorizontalDivider()
    }
}

@Preview
@Composable
fun TopicListItemPreview() {
    MemTopicTheme {
        TopicListItem(
            topic = sampleTopic00,
            navController = rememberNavController(),
            topicViewModel = MockTopicViewModel(),
        )
    }
}
