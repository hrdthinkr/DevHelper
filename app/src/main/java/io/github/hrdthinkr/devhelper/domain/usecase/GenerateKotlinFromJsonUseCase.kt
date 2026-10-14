package io.github.hrdthinkr.devhelper.domain.usecase

import io.github.hrdthinkr.devhelper.domain.generator.JsonToKotlinGenerator
import jakarta.inject.Inject

class GenerateKotlinFromJsonUseCase @Inject constructor(
    private val generator: JsonToKotlinGenerator
) {
    operator fun invoke(
        json: String,
        className: String
    ): String {
        require(json.isNotBlank()) {
            "Enter JSON to generate Kotlin code"
        }

        require(className.isNotBlank()) {
            "Enter a class name"
        }

        return generator.generate(json, className)
    }
}
