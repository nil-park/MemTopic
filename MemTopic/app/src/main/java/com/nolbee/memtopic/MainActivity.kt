package com.nolbee.memtopic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.nolbee.memtopic.account_view.AccountViewTopAppBar
import com.nolbee.memtopic.database.TopicDatabase
import com.nolbee.memtopic.database.TopicRepository
import com.nolbee.memtopic.database.TopicViewModel
import com.nolbee.memtopic.database.TopicViewModelFactory
import com.nolbee.memtopic.edit_topic_view.EditTopicViewTopAppBar
import com.nolbee.memtopic.topic_list_view.MockTopicDao
import com.nolbee.memtopic.topic_list_view.TopicListTopAppBar
import com.nolbee.memtopic.ui.theme.MemTopicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val topicDb = Room.databaseBuilder(
            applicationContext,
            TopicDatabase::class.java,
            "topicDatabase"
        ).build()
        val topicDao = topicDb.topicDao()
        val topicRepository = TopicRepository(topicDao)
        val topicViewModel = ViewModelProvider(
            this,
            TopicViewModelFactory(topicRepository)
        )[TopicViewModel::class.java]
        setContent {
            MemTopicTheme {
                MainView(topicViewModel)
            }
        }
    }
}

@Composable
fun MainView(
    topicViewModel: TopicViewModel = TopicViewModel(TopicRepository(MockTopicDao()))
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
                        topicViewModel = topicViewModel,
                    )
                }
                composable(
                    "EditTopicView",
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None }
                ) {
                    EditTopicViewTopAppBar(
                        navController = navController,
                        topicViewModel = topicViewModel,
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
        MainView()
    }
}
