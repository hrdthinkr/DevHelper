package io.github.hrdthinkr.devhelper.presentation.screen.home

import io.github.hrdthinkr.devhelper.domain.model.DevTool

data class HomeScreenState(
    val searchQuery: String = "",
    val tools: List<DevTool> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val error: String? = null
)
