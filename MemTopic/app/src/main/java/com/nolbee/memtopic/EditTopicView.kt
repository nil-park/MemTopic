package com.nolbee.memtopic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
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
import com.nolbee.memtopic.ui.theme.MemTopicTheme

private const val TAG = "AddTopicView"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTopicViewTopAppBar(
    onClickNavigationIcon: () -> Unit = {},
    viewModel: EditTopicViewModel = EditTopicViewModel(),
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
                navigationIcon = {
                    IconButton(onClick = onClickNavigationIcon) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (viewModel.isModified) {
                        IconButton(
                            onClick = {

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
                TopicTitleTextField(viewModel)
                TopicContentTextField(viewModel)
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
        modifier = Modifier.fillMaxSize(1f).verticalScroll(scrollState),
        singleLine = false,
        label = { Text("내용") },  // TODO: replace this string with a string resource to achieve multi-language support.
        visualTransformation = VisualTransformation.None,
    )
}

@Preview(showBackground = true)
@Composable
fun EditTopicPreview() {
    MemTopicTheme {
        EditTopicViewTopAppBar()
    }
}
