package com.nolbee.memtopic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
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
import com.nolbee.memtopic.database.Topic
import com.nolbee.memtopic.database.TopicDao
import com.nolbee.memtopic.database.TopicRepository
import com.nolbee.memtopic.database.TopicViewModel
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicListTopAppBar(
    onClickNavigationIcon: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
    topicViewModel: TopicViewModel = TopicViewModel(TopicRepository(MockTopicDao())),
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
                navigationIcon = {
                    IconButton(onClick = onClickNavigationIcon) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null
                        )
                    }
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
private fun TopicList(viewModel: TopicViewModel, innerPadding: PaddingValues) {
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
        TopicListTopAppBar()
    }
}

class MockTopicDao : TopicDao {
    private val topics = listOf(sampleTopic00, sampleTopic01)
    override suspend fun upsertTopic(topic: Topic) {}
    override suspend fun deleteTopic(topic: Topic) {}
    override fun selectTopicByName(): Flow<List<Topic>> = flowOf(topics)
    override fun selectTopicByLastModified(): Flow<List<Topic>> = flowOf(topics)
    override fun selectTopicByLastPlayback(): Flow<List<Topic>> = flowOf(topics)
}

