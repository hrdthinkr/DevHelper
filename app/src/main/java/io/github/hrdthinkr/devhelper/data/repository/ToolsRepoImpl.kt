import io.github.hrdthinkr.devhelper.core.common.Resource
import io.github.hrdthinkr.devhelper.domain.model.DevTool
import io.github.hrdthinkr.devhelper.domain.model.ToolCategory
import io.github.hrdthinkr.devhelper.domain.repository.ToolsRepo
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class ToolsRepoImpl @Inject constructor() : ToolsRepo {

    override fun getTools(): Resource<List<DevTool>> {
        return try {
            Resource.Success(
                data = listOf(
                    DevTool(
                        id = "json_to_kotlin",
                        name = "JSON to Kotlin",
                        description = "Generate Kotlin data classes from JSON",
                        icon = "{ }",
                        category = ToolCategory.GENERATION
                    ),
                    DevTool(
                        id = "compare_api",
                        name = "Compare API Responses",
                        description = "Compare JSON responses and detect changes",
                        icon = "⇄",
                        category = ToolCategory.COMPARISON
                    ),
                    DevTool(
                        id = "saved_contracts",
                        name = "Saved Contracts",
                        description = "Save and manage API contracts",
                        icon = "▤",
                        category = ToolCategory.STORAGE
                    )
                )
            )
        } catch (e: Exception) {
            Resource.Error(
                message = e.message ?: "Failed to load tools",
                cause = e
            )
        }
    }
}
