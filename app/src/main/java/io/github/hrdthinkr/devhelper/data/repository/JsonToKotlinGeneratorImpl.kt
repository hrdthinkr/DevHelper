package io.github.hrdthinkr.devhelper.data.repository

import io.github.hrdthinkr.devhelper.domain.generator.JsonToKotlinGenerator
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Singleton
class JsonToKotlinGeneratorImpl @Inject constructor() :
    JsonToKotlinGenerator {

    override fun generate(
        json: String,
        className: String
    ): String {
        val root = try {
            Json.parseToJsonElement(json)
        } catch (_: Exception) {
            throw IllegalArgumentException("Invalid JSON")
        }

        require(root is JsonObject) {
            "The root JSON value must be an object"
        }

        val classes = linkedMapOf<String, String>()
        generateClass(className.toClassName(), root, classes)

        return classes.values.joinToString("\n\n")
    }

    private fun generateClass(
        name: String,
        json: JsonObject,
        classes: MutableMap<String, String>
    ) {
        val properties = json.map { (key, value) ->
            val propertyName = key.toPropertyName()
            val type = inferType(
                propertyName.toClassName(),
                value,
                classes
            )

            "    val $propertyName: $type"
        }

        classes[name] = buildString {
            appendLine("data class $name(")
            append(properties.joinToString(",\n"))
            appendLine()
            append(")")
        }
    }

    private fun inferType(
        name: String,
        value: JsonElement,
        classes: MutableMap<String, String>
    ): String {
        return when (value) {
            is JsonObject -> {
                val nestedName = name.ifBlank { "NestedObject" }
                generateClass(nestedName, value, classes)
                nestedName
            }

            is JsonArray -> inferArrayType(name, value, classes)

            is JsonNull -> "Any?"

            is JsonPrimitive -> when {
                value.isString -> "String"
                value.content == "true" ||
                        value.content == "false" -> "Boolean"

                value.content.toIntOrNull() != null -> "Int"
                value.content.toLongOrNull() != null -> "Long"
                value.content.toDoubleOrNull() != null -> "Double"
                else -> "Any?"
            }
        }
    }

    private fun inferArrayType(
        name: String,
        array: JsonArray,
        classes: MutableMap<String, String>
    ): String {
        if (array.isEmpty()) return "List<Any?>"

        val types = array.map {
            inferType(name, it, classes)
        }.distinct()

        val elementType = types.singleOrNull() ?: "Any?"

        return "List<$elementType>"
    }

    private fun String.toClassName(): String {
        val words = split(Regex("[^A-Za-z0-9]+"))
            .filter { it.isNotBlank() }

        val result = words.joinToString("") { word ->
            word.replaceFirstChar { it.uppercase() }
        }

        return when {
            result.isBlank() -> "GeneratedModel"
            result.first().isDigit() -> "Model$result"
            else -> result
        }
    }

    private fun String.toPropertyName(): String {
        val normalized = replace(Regex("[^A-Za-z0-9_]"), "_")
            .ifBlank { "field" }

        val safe = if (normalized.first().isDigit()) {
            "_$normalized"
        } else {
            normalized
        }

        return if (safe in KOTLIN_KEYWORDS) {
            "`$safe`"
        } else {
            safe
        }
    }

    private companion object {
        val KOTLIN_KEYWORDS = setOf(
            "as", "break", "class", "continue", "do", "else",
            "false", "for", "fun", "if", "in", "interface",
            "is", "null", "object", "package", "return", "super",
            "this", "throw", "true", "try", "typealias", "typeof",
            "val", "var", "when", "while"
        )
    }
}
