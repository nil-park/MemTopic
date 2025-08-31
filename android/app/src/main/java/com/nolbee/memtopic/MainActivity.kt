package com.nolbee.memtopic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import com.nolbee.memtopic.account_view.AccountViewTopAppBar
import com.nolbee.memtopic.database.ITopicViewModel
import com.nolbee.memtopic.database.MockTopicViewModel
import com.nolbee.memtopic.database.PlaybackRepository
import com.nolbee.memtopic.database.TopicRepository
import com.nolbee.memtopic.database.TopicViewModel
import com.nolbee.memtopic.edit_topic_view.EditTopicViewModel
import com.nolbee.memtopic.edit_topic_view.EditTopicViewTopAppBar
import com.nolbee.memtopic.play_topic_view.IPlayTopicViewModel
import com.nolbee.memtopic.play_topic_view.PlayTopicViewModel
import com.nolbee.memtopic.play_topic_view.PlayTopicViewTopAppBar
import com.nolbee.memtopic.topic_list_view.TopicListTopAppBar
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var playbackRepository: PlaybackRepository

    @Inject
    lateinit var topicRepository: TopicRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemTopicTheme {
                val topicViewModel: ITopicViewModel = hiltViewModel<TopicViewModel>()
                MainView(
                    topicViewModel = topicViewModel,
                    playbackRepository = playbackRepository,
                    topicRepository = topicRepository,
                )
            }
        }
    }
}

@Composable
fun MainView(
    topicViewModel: ITopicViewModel,
    playbackRepository: PlaybackRepository? = null,
    topicRepository: TopicRepository? = null,
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    var startDestination by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    
    // Double-back to exit state
    var backPressedTime by remember { mutableStateOf(0L) }
    val backPressedThreshold = 2000L // 2 seconds
    
    // Get current destination
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    // Handle back button press for TopicList only - double back to exit
    BackHandler(enabled = currentDestination == "TopicList") {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < backPressedThreshold) {
            // Second back press within threshold - exit app
            (context as? ComponentActivity)?.finish()
        } else {
            // First back press - just record time, no toast
            backPressedTime = currentTime
        }
    }

    LaunchedEffect(Unit) {
        val playback = playbackRepository?.getPlaybackOnce()
        if (playback != null && playback.topicId != 0) {
            topicRepository?.getTopic(playback.topicId)?.let { topic ->
                topicViewModel.topicToPlay = topic
                startDestination = "PlayTopicView"
                return@LaunchedEffect
            }
        }
        startDestination = "TopicList"
    }

    if (startDestination == null) {
        Text("Loading...")
    } else {
        ModalNavigationDrawerMain(
            drawerState = drawerState,
            navController = navController,
            navHost = {
                NavHost(navController = navController, startDestination = startDestination!!) {
                    composable(
                        "AccountView",
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
                        val editTopicViewModel = hiltViewModel<EditTopicViewModel>()
                        LaunchedEffect(Unit) {
                            editTopicViewModel.setTopicReference(topicViewModel.topicToEdit)
                        }
                        EditTopicViewTopAppBar(
                            navController = navController,
                            topicViewModel = topicViewModel,
                            editTopicViewModel = editTopicViewModel,
                        )
                    }
                    composable(
                        "PlayTopicView",
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None }
                    ) {
                        val playTopicViewModel: IPlayTopicViewModel =
                            hiltViewModel<PlayTopicViewModel>()
                        LaunchedEffect(Unit) {
                            playTopicViewModel.setTopic(topicViewModel.topicToPlay)
                        }
                        PlayTopicViewTopAppBar(
                            vm = playTopicViewModel,
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    MemTopicTheme {
        MainView(MockTopicViewModel())
    }
}
