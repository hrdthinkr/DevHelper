package io.github.hrdthinkr.devhelper.presentation.screen.home

import androidx.lifecycle.ViewModel
import io.github.hrdthinkr.devhelper.domain.model.DevTool
import io.github.hrdthinkr.devhelper.domain.model.ToolCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    init {
        loadTools()
    }

    private fun loadTools() {
        val tools = listOf(
            DevTool(
                id = "json",
                name = "JSON Formatter",
                description = "Format and validate JSON",
                icon = "{ }",
                category = ToolCategory.DATA
            ),
            DevTool(
                id = "diff",
                name = "JSON Diff",
                description = "Compare two JSON objects",
                icon = "⇄",
                category = ToolCategory.DATA
            ),
            DevTool(
                id = "base64",
                name = "Base64 Converter",
                description = "Encode and decode text",
                icon = "↔",
                category = ToolCategory.ENCODING
            ),
            DevTool(
                id = "uuid",
                name = "UUID Generator",
                description = "Generate unique identifiers",
                icon = "#",
                category = ToolCategory.GENERATORS
            ),
            DevTool(
                id = "timestamp",
                name = "Timestamp Converter",
                description = "Convert Unix timestamps",
                icon = "◷",
                category = ToolCategory.TIME
            )
        )

        _uiState.update { currentState ->
            currentState.copy(tools = tools)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = query)
        }
    }
}
