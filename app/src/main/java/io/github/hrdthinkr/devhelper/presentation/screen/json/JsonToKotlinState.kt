package io.github.hrdthinkr.devhelper.presentation.screen.json

data class JsonToKotlinUiState(
    val jsonInput: String = "",
    val className: String = "MyData",
    val generatedCode: String = "",
    val errorMessage: String? = null,
    val isGenerating: Boolean = false
)
