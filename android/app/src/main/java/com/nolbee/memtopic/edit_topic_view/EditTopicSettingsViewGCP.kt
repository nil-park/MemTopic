package com.nolbee.memtopic.edit_topic_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditTopicSettingsViewGCP(vm: EditTopicViewModel) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState()
    var expandLanguageCodes by remember { mutableStateOf(false) }
    var expandVoiceCodes by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // TODO: Exception Handling
        vm.loadLanguageCodes(context)
        vm.loadVoiceCodes(context)
    }

    ModalBottomSheet(
        onDismissRequest = { vm.openBottomSheet = false },
        sheetState = bottomSheetState
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "언어 코드: ",
                )
                Button(
                    onClick = {
                        expandLanguageCodes = !expandLanguageCodes
                        if (expandLanguageCodes) {
                            expandVoiceCodes = false
                        }
                    },
                ) { Text(text = vm.selectedLanguageCode) }
            }
            if (expandLanguageCodes) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        vm.languageCodes.forEach { langCode ->
                            Box(modifier = Modifier.padding(end = 8.dp)) {
                                FilterChip(
                                    selected = (langCode == vm.selectedLanguageCode),
                                    onClick = {
                                        if (langCode != vm.selectedLanguageCode) {
                                            vm.updateVoiceType(langCode, "")
                                            vm.loadVoiceCodes(context)
                                            expandLanguageCodes = false
                                            expandVoiceCodes = true
                                        } else {
                                            expandLanguageCodes = false
                                        }
                                    },
                                    label = { Text(langCode) },
                                    leadingIcon = if (langCode == vm.selectedLanguageCode) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Done,
                                                contentDescription = "Selected Language Code",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "음성 코드: ",
                )
                Button(
                    onClick = {
                        expandVoiceCodes = !expandVoiceCodes
                        if (expandVoiceCodes) {
                            expandLanguageCodes = false
                        }
                        vm.loadVoiceCodes(context)
                    },
                ) { Text(text = vm.selectedVoiceType) }
            }
            if (expandVoiceCodes) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        vm.voiceTypes.forEach { voiceCode ->
                            Box(modifier = Modifier.padding(end = 8.dp)) {
                                FilterChip(
                                    selected = (voiceCode == vm.selectedVoiceType),
                                    onClick = {
                                        vm.updateVoiceType(vm.selectedLanguageCode, voiceCode)
                                        expandVoiceCodes = false
                                    },
                                    label = { Text(voiceCode) },
                                    leadingIcon = if (voiceCode == vm.selectedVoiceType) {
                                        {
                                            Icon(
                                                imageVector = Icons.Filled.Done,
                                                contentDescription = "Selected Voice Code",
                                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
