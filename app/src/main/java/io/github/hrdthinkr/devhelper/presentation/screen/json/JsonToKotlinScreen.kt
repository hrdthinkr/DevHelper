package io.github.hrdthinkr.devhelper.presentation.screen.json

import android.content.ClipData
import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.hrdthinkr.devhelper.presentation.components.KotlinCodeText
import kotlinx.coroutines.launch

@Composable
fun JsonToKotlinScreen(
    onBackClick: () -> Unit,
    viewModel: JsonToKotlinViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onBackClick) {
                    Text("← Back")
                }

                Text(
                    text = "DEVPOCKET",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Text(
                text = "JSON to Kotlin",
                style = MaterialTheme.typography.headlineMedium
            )

            Text("Convert JSON into Kotlin data classes")

            OutlinedTextField(
                value = uiState.className,
                onValueChange = viewModel::onClassNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Root class name") },
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.jsonInput,
                onValueChange = viewModel::onJsonInputChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("JSON input") },
                placeholder = {
                    Text("""{"name": "Alex", "age": 20}""")
                },
                minLines = 6
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = viewModel::generate,
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isGenerating
                ) {
                    Text(
                        if (uiState.isGenerating) "Generating..."
                        else "Generate"
                    )
                }

                TextButton(
                    onClick = viewModel::loadExample
                ) {
                    Text("Example")
                }
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }


            if (uiState.generatedCode.isNotBlank()) {
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Generated Kotlin",
                                style = MaterialTheme.typography.titleMedium
                            )

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        clipboard.setClipEntry(
                                            ClipEntry(
                                                ClipData.newPlainText(
                                                    "Generated Kotlin",
                                                    uiState.generatedCode
                                                )
                                            )
                                        )

                                        Toast.makeText(
                                            context,
                                            "Code copied!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            ) {
                                Text(
                                    text = "copy",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }

                        SelectionContainer {
                            KotlinCodeText(uiState.generatedCode)
                        }
                    }
                }
            }

        }
    }

}
