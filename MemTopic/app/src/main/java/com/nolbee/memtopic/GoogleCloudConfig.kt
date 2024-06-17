package com.nolbee.memtopic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun GoogleCloudConfigView(viewModel: ConfigViewModel) {
    Box(
        Modifier
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()

        ) {
            Surface {
                Text(
                    "구글 클라우드 설정",  // TODO: replace this string with a string resource to achieve multi-language support.
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                )
            }
            TTSTokenTextField(viewModel)
        }
    }
}

@Composable
private fun TTSTokenTextField(viewModel: ConfigViewModel) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    TextField(
        value = viewModel.gcpTextToSpeechToken,
        onValueChange = { viewModel.updateGcpTextToSpeechToken(it) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("TTS 토큰") },  // TODO: replace this string with a string resource to achieve multi-language support.
        visualTransformation =
        if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val visibilityIcon =
                    if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                Icon(imageVector = visibilityIcon, contentDescription = null)
            }
        },
    )
}

@Preview
@Composable
private fun GoogleCloudConfigViewPreview() {
    val viewModel = DummyConfigViewModel()
    GoogleCloudConfigView(viewModel)
}
