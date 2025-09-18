package com.nolbee.memtopic.utils

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nolbee.memtopic.database.ITopicViewModel
import com.nolbee.memtopic.database.MockTopicRepository
import com.nolbee.memtopic.database.MockTopicViewModel
import com.nolbee.memtopic.ui.theme.MemTopicTheme
import kotlinx.coroutines.launch

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ExportImportView(
    navController: NavHostController,
    topicViewModel: ITopicViewModel,
    topicExporter: TopicExporter,
    topicImporter: TopicImporter
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val topics by topicViewModel.topics.collectAsState(initial = emptyList())

    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }
    var showPermissionInfoDialog by remember { mutableStateOf(false) }

    // Export file launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                scope.launch {
                    isExporting = true
                    try {
                        val jsonData = topicExporter.topicsToJson(topics)
                        val success = topicExporter.writeJsonToUri(uri, jsonData)

                        if (success) {
                            Toast.makeText(
                                context,
                                "${topics.size}개의 토픽을 내보냈습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "파일 저장에 실패했습니다",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "내보내기 중 오류가 발생했습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        isExporting = false
                    }
                }
            }
        } else {
            // User cancelled or permission denied
            isExporting = false
            if (result.resultCode == android.app.Activity.RESULT_CANCELED) {
                // User intentionally cancelled, no need to show error
            } else {
                showPermissionInfoDialog = true
            }
        }
    }

    // Import file launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                scope.launch {
                    isImporting = true
                    try {
                        when (val importResult = topicImporter.importTopicsFromUri(uri)) {
                            is ImportResult.Success -> {
                                Toast.makeText(
                                    context,
                                    "${importResult.importedCount}개의 토픽을 가져왔습니다",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is ImportResult.Error -> {
                                Toast.makeText(
                                    context,
                                    importResult.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "가져오기 중 오류가 발생했습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        isImporting = false
                    }
                }
            }
        } else {
            // User cancelled or permission denied
            isImporting = false
            if (result.resultCode == android.app.Activity.RESULT_CANCELED) {
                // User intentionally cancelled, no need to show error
            } else {
                showPermissionInfoDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("토픽 내보내기/가져오기") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Export Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "토픽 내보내기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "현재 ${topics.size}개의 토픽이 있습니다",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isExporting) {
                        CircularProgressIndicator()
                    } else {
                        Button(
                            onClick = {
                                if (topics.isNotEmpty()) {
                                    isExporting = true
                                    exportLauncher.launch(topicExporter.createSaveFileIntent())
                                } else {
                                    Toast.makeText(
                                        context,
                                        "내보낼 토픽이 없습니다",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = topics.isNotEmpty()
                        ) {
                            Text("토픽 내보내기")
                        }
                    }
                }
            }

            // Import Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "토픽 가져오기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "JSON 파일에서 토픽을 가져옵니다\n중복된 제목은 자동으로 번호가 추가됩니다",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isImporting) {
                        CircularProgressIndicator()
                    } else {
                        OutlinedButton(
                            onClick = {
                                isImporting = true
                                importLauncher.launch(topicImporter.createOpenFileIntent())
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("파일 선택")
                        }
                    }
                }
            }
        }
    }

    // Permission info dialog
    if (showPermissionInfoDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionInfoDialog = false },
            title = { Text("파일 접근 안내") },
            text = {
                Text(
                    "파일을 저장하거나 불러오기 위해서는 파일 접근 권한이 필요합니다.\n\n" +
                            "• 내보내기: 저장할 위치를 선택해주세요\n" +
                            "• 가져오기: 불러올 JSON 파일을 선택해주세요\n\n" +
                            "파일 선택기에서 원하는 위치나 파일을 선택하시면 됩니다."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showPermissionInfoDialog = false }
                ) {
                    Text("확인")
                }
            }
        )
    }
}

@Preview
@Composable
fun ExportImportViewPreview() {
    val context = LocalContext.current
    MemTopicTheme {
        ExportImportView(
            navController = rememberNavController(),
            topicViewModel = MockTopicViewModel(),
            topicExporter = TopicExporter(context),
            topicImporter = TopicImporter(context, MockTopicRepository())
        )
    }
}