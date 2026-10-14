package io.github.hrdthinkr.devhelper.domain.repository

import io.github.hrdthinkr.devhelper.core.common.Resource
import io.github.hrdthinkr.devhelper.domain.model.DevTool

interface ToolsRepo {
    fun getTools(): Resource<List<DevTool>>
}
