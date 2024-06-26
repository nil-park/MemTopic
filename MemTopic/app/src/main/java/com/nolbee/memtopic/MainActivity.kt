package com.nolbee.memtopic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nolbee.memtopic.account_view.AccountViewTopAppBar
import com.nolbee.memtopic.database.ITopicViewModel
import com.nolbee.memtopic.database.MockTopicViewModel
import com.nolbee.memtopic.database.TopicViewModel
import com.nolbee.memtopic.edit_topic_view.EditTopicViewModel
import com.nolbee.memtopic.edit_topic_view.EditTopicViewTopAppBar
import com.nolbee.memtopic.topic_list_view.TopicListTopAppBar
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemTopicTheme {
                val topicViewModel: ITopicViewModel = hiltViewModel<TopicViewModel>()
                MainView(topicViewModel = topicViewModel)
            }
        }
    }
}

@Composable
fun MainView(
    topicViewModel: ITopicViewModel
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    ModalNavigationDrawerMain(
        drawerState = drawerState,
        navController = navController,
        navHost = {
            NavHost(navController = navController, startDestination = "TopicList") {
                composable(
                    "ConfigView",
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    AccountViewTopAppBar()
                }
                composable(
                    "TopicList",
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    TopicListTopAppBar(
                        navController = navController,
                        topicViewModel = topicViewModel
                    )
                }
                composable(
                    "EditTopicView",
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    val editTopicViewModel: EditTopicViewModel = viewModel()
                    LaunchedEffect(Unit) {
                        editTopicViewModel.setTopicReference(topicViewModel.topicToEdit)
                    }
                    EditTopicViewTopAppBar(
                        navController = navController,
                        topicViewModel = topicViewModel,
                        editTopicViewModel = editTopicViewModel,
                    )
                }
            }
        }
    )
}

private const val TAG = "MainActivity"

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    MemTopicTheme {
        MainView(MockTopicViewModel())
    }
}
