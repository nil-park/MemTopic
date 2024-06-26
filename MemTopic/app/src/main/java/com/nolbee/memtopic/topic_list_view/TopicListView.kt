package com.nolbee.memtopic.topic_list_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
