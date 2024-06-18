package com.nolbee.memtopic

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import kotlinx.coroutines.launch

class ConfigViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfigViewModel::class.java)) {
            val viewModel = ConfigViewModel(application)
            viewModel.initSecureStore()
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val configViewModel = ViewModelProvider(
            this,
            ConfigViewModelFactory(application)
        )[ConfigViewModel::class.java]
        setContent {
            MemTopicTheme {
                MainView(configViewModel)
            }
        }
    }
}

@Composable
fun MainView(
    configViewModel: ConfigViewModel = ConfigViewModel(Application()),
    editTopicViewModel: EditTopicViewModel = EditTopicViewModel(Application())
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val onClickNavigationIcon: () -> Unit = { scope.launch { drawerState.open() } }
    ModalNavigationDrawerMain(
        drawerState = drawerState,
        navController = navController,
        navHost = {
            NavHost(navController = navController, startDestination = "TopicList") {
                composable("ConfigView") {
                    ConfigViewTopAppBar(
                        onClickNavigationIcon = onClickNavigationIcon,
                        viewModel = configViewModel
                    )
                }
                composable("TopicList") {
                    TopicListTopAppBar(
                        onClickNavigationIcon = onClickNavigationIcon,
                        navController = navController,
                        editTopicViewModel = editTopicViewModel
                    )
                }
                composable("EditTopicView") {
                    EditTopicViewTopAppBar(
                        onClickNavigationIcon = onClickNavigationIcon,
                        viewModel = editTopicViewModel
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
