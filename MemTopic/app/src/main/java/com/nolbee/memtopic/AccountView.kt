package com.nolbee.memtopic

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nolbee.memtopic.ui.theme.MemTopicTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountViewTopAppBar(
    onClickNavigationIcon: () -> Unit = {},
    viewModel: AccountViewModelInterface = viewModel(
        factory = AccountViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "계정 관리",  // TODO: replace this string with a string resource to achieve multi-language support.
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
                    if (viewModel.gcpIsTextToSpeechTokenModified) {
                        IconButton(
                            onClick = {
                                viewModel.saveGcpTextToSpeechToken()
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
                GoogleCloudAccountView(viewModel)
            }
        }
    )
}

@Preview
@Composable
fun ConfigViewTopAppBarPreview() {
    MemTopicTheme {
        AccountViewTopAppBar(viewModel = AccountViewModelInterface(Application()))
    }
}
