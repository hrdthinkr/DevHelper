package io.github.hrdthinkr.devhelper.domain.model

data class DevTool (
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val category: ToolCategory
)