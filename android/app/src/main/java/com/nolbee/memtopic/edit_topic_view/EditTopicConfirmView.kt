package com.nolbee.memtopic.edit_topic_view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nolbee.memtopic.ui.theme.MemTopicTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmView(
    content: String,
    vm: EditTopicViewModel,
    onConfirm: () -> Unit,
) {
    if (vm.isConfirmDialogOpen) {
        BasicAlertDialog(
            onDismissRequest = {
                vm.isConfirmDialogOpen = false
            }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = content,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = {
                            onConfirm()
                            vm.isConfirmDialogOpen = false
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmViewPreview() {
    MemTopicTheme {
        val vm = EditTopicViewModel()
        vm.isConfirmDialogOpen = true
        ConfirmView(
            content = "Do you really want to do so?",
            vm = vm,
            onConfirm = {}
        )
    }
}
