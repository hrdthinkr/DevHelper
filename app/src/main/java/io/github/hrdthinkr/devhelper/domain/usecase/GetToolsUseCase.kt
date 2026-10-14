package io.github.hrdthinkr.devhelper.domain.usecase

import io.github.hrdthinkr.devhelper.core.common.Resource
import io.github.hrdthinkr.devhelper.domain.model.DevTool
import io.github.hrdthinkr.devhelper.domain.repository.ToolsRepo
import jakarta.inject.Inject

class GetToolsUseCase @Inject constructor(
    private val toolsRepo: ToolsRepo
) {
    operator fun invoke(): Resource<List<DevTool>> {
        return toolsRepo.getTools()
    }
}
