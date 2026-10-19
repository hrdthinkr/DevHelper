package io.github.hrdthinkr.devhelper.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

@Composable
fun KotlinCodeText(code: String) {
    Text(
        text = code,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace
        )
    )
}
