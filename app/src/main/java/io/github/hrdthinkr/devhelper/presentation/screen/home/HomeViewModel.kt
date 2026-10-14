
package io.github.hrdthinkr.devhelper.presentation.screen.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.hrdthinkr.devhelper.core.common.Resource
import io.github.hrdthinkr.devhelper.domain.usecase.GetToolsUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getToolsUseCase: GetToolsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTools()
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(searchQuery = query)
        }
    }

    fun retryLoading() {
        loadTools()
    }

    private fun loadTools() {
        _uiState.update { state ->
            state.copy(
                isLoading = true,
                error = null
            )
        }

        when (val result = getToolsUseCase()) {
            is Resource.Success -> {
                _uiState.update { state ->
                    state.copy(
                        tools = result.data,
                        isLoading = false,
                        error = null
                    )
                }
            }

            is Resource.Error -> {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }

            is Resource.Loading -> {
                _uiState.update { state ->
                    state.copy(isLoading = true)
                }
            }
        }
    }
}
