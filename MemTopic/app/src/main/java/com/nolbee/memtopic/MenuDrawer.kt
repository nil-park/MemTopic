package com.nolbee.memtopic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import kotlinx.coroutines.launch

@Composable
private fun NavigationContentSample() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("This is a sample content")
    }
}

@Composable
fun ModalNavigationDrawerMain(
    drawerState: DrawerState,
    accountContent: @Composable () -> Unit = { NavigationContentSample() },
    topicListContent: @Composable () -> Unit = { NavigationContentSample() },
    settingsContent: @Composable () -> Unit = { NavigationContentSample() },
) {
    val scope = rememberCoroutineScope()
    val selectedItem = remember { mutableStateOf(topicListContent) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(20.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ManageAccounts, contentDescription = null) },
                        label = { Text("계정 관리") }, // TODO: replace this string with a string resource to achieve multi-language support.
                        selected = accountContent == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = accountContent
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.FormatListBulleted,
                                contentDescription = null
                            )
                        },
                        label = { Text("토픽 리스트") }, // TODO: replace this string with a string resource to achieve multi-language support.
                        selected = topicListContent == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = topicListContent
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text("플레이 설정") }, // TODO: replace this string with a string resource to achieve multi-language support.
                        selected = settingsContent == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = settingsContent
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            val innerContent: @Composable () -> Unit = selectedItem.value
            innerContent()
        }
    )
}


@Preview
@Composable
fun ModalNavigationDrawerMainPreview() {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    MemTopicTheme {
        ModalNavigationDrawerMain(
            drawerState = drawerState
        )
    }
}