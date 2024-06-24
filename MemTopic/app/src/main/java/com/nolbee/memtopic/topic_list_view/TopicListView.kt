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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nolbee.memtopic.database.ITopicViewModel
import com.nolbee.memtopic.database.MockTopicViewModel
import com.nolbee.memtopic.database.TopicViewModel
import com.nolbee.memtopic.edit_topic_view.EditTopicViewModel
import com.nolbee.memtopic.ui.theme.MemTopicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicListTopAppBar(
    navController: NavHostController = rememberNavController(),
    topicViewModel: ITopicViewModel = hiltViewModel<TopicViewModel>(),
    editTopicViewModel: EditTopicViewModel = EditTopicViewModel()
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
                        editTopicViewModel.prepareAddNewTopic()
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
                viewModel = topicViewModel,
                innerPadding = innerPadding,
            )
        }
    )
}

@Composable
private fun TopicList(viewModel: ITopicViewModel, innerPadding: PaddingValues) {
    val topics by viewModel.topics.collectAsState(initial = emptyList())

    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(topics) { topic ->
            TopicListItem(topic = topic)
        }
    }
}

@Preview
@Composable
fun TopicListTopAppBarPreview() {
    MemTopicTheme {
        TopicListTopAppBar(
            topicViewModel = MockTopicViewModel()
        )
    }
}
