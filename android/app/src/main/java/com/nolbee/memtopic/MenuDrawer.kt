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
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.ManageAccounts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
    navController: NavHostController,
    navHost: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(20.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ManageAccounts, contentDescription = null) },
                        label = { Text("계정 관리") }, // TODO: replace this string with a string resource to achieve multi-language support.
                        selected = navController.getCurrentRoute() == "AccountView",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("AccountView") {
                                popUpTo("TopicList") { inclusive = false }
                                launchSingleTop = true
                            }
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
                        selected = navController.getCurrentRoute() == "TopicList",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("TopicList") {
                                popUpTo("TopicList") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.ImportExport, contentDescription = null) },
                        label = { Text("토픽 내보내기/가져오기") }, // TODO: replace this string with a string resource to achieve multi-language support.
                        selected = navController.getCurrentRoute() == "ExportImportView",
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("ExportImportView") {
                                popUpTo("TopicList") { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            navHost()
        }
    )
}

@Composable
fun NavController.getCurrentRoute(): String? {
    val navBackStackEntry by currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Preview
@Composable
fun ModalNavigationDrawerMainPreview() {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val navController = rememberNavController()
    MemTopicTheme {
        ModalNavigationDrawerMain(
            drawerState = drawerState,
            navController = navController,
            navHost = {
                NavHost(navController = navController, startDestination = "TopicList") {
                    composable("AccountView") { NavigationContentSample() }
                    composable("TopicList") { NavigationContentSample() }
                    composable("EditTopicView") { NavigationContentSample() }
                    composable("ExportImportView") { NavigationContentSample() }
                }
            }
        )
    }
}