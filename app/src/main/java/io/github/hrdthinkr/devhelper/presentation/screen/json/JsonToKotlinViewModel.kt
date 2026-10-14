package io.github.hrdthinkr.devhelper.presentation.screen.json

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.hrdthinkr.devhelper.domain.usecase.GenerateKotlinFromJsonUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class JsonToKotlinViewModel @Inject constructor(
    private val generateKotlinFromJson: GenerateKotlinFromJsonUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(JsonToKotlinUiState())
    val uiState = _uiState.asStateFlow()

    fun onJsonInputChange(value: String) {
        _uiState.update {
            it.copy(
                jsonInput = value,
                errorMessage = null
            )
        }
    }

    fun onClassNameChange(value: String) {
        _uiState.update {
            it.copy(
                className = value,
                errorMessage = null
            )
        }
    }

    fun loadExample() {
        _uiState.update {
            it.copy(
                jsonInput = """{
    "name": "Alex",
    "age": 20,
    "isDeveloper": true,
    "address": {
        "city": "Novosibirsk"
    }
}""",
                className = "User",
                generatedCode = "",
                errorMessage = null
            )
        }
    }

    fun generate() {
        val state = _uiState.value

        if (state.jsonInput.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Enter JSON to generate Kotlin code")
            }
            return
        }

        if (state.className.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Enter a class name")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isGenerating = true,
                    errorMessage = null
                )
            }

            try {
                val result = withContext(Dispatchers.Default) {
                    generateKotlinFromJson(
                        json = state.jsonInput,
                        className = state.className
                    )
                }

                _uiState.update {
                    it.copy(generatedCode = result)
                }
            } catch (exception: IllegalArgumentException) {
                _uiState.update {
                    it.copy(
                        errorMessage = exception.message
                            ?: "Invalid JSON or class name"
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to generate Kotlin code"
                    )
                }
            } finally {
                _uiState.update {
                    it.copy(isGenerating = false)
                }
            }
        }
    }
}
