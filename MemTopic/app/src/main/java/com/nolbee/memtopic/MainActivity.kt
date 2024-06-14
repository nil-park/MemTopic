package com.nolbee.memtopic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemTopicTheme {
                MainView()
            }
        }
    }
}

@Composable
fun MainView() {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val onClickNavigationIcon: () -> Unit = { scope.launch { drawerState.open() } }
    ModalNavigationDrawerMain(
        drawerState = drawerState,
        accountContent = {
            ConfigView()
        },
        topicListContent = {
            TopicListTopAppBar(onClickNavigationIcon)
        },
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
