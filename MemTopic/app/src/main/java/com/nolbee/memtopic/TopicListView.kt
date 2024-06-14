package com.nolbee.memtopic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nolbee.memtopic.ui.theme.MemTopicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicListTopAppBar(onClickNavigationIcon: () -> Unit) {
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
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val list = (0..0).map { it.toString() }
                items(count = list.size) {
                    TopicListItem(
                        sampleTopic
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun TopicListTopAppBarPreview() {
    val onClickNavigationIcon: () -> Unit = {}
    MemTopicTheme {
        TopicListTopAppBar(onClickNavigationIcon)
    }
}