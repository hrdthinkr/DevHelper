
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
        val usedClassNames = mutableSetOf<String>()

        generateClassFromObjects(
            className.toClassName(),
            listOf(root),
            classes,
            usedClassNames
        )

        return classes.values.joinToString("\n\n")
    }

    private fun generateClassFromObjects(
        name: String,
        objects: List<JsonObject>,
        classes: MutableMap<String, String>,
        usedClassNames: MutableSet<String>
    ): String {
        val className = uniqueClassName(name, usedClassNames)
        val usedPropertyNames = mutableSetOf<String>()

        val keys = objects
            .flatMap { it.keys }
            .distinct()

        val properties = keys.map { key ->
            val propertyName = key.toPropertyName()

            var uniquePropertyName = propertyName
            var suffix = 2

            while (!usedPropertyNames.add(uniquePropertyName)) {
                uniquePropertyName = "${propertyName}${suffix}"
                suffix++
            }

            val values = objects.mapNotNull { it[key] }

            val isMissingInSomeObjects =
                values.size < objects.size

            val type = inferMergedType(
                uniquePropertyName.toClassName(),
                values,
                classes,
                usedClassNames,
                isMissingInSomeObjects
            )

            "    val $uniquePropertyName: $type"
        }

        classes[className] = buildString {
            appendLine("data class $className(")
            append(properties.joinToString(",\n"))
            appendLine()
            append(")")
        }

        return className
    }

    private fun uniqueClassName(
        name: String,
        usedNames: MutableSet<String>
    ): String {
        if (usedNames.add(name)) return name

        var suffix = 2

        while (!usedNames.add("${name}${suffix}")) {
            suffix++
        }

        return "${name}${suffix}"
    }

    private fun inferMergedType(
        name: String,
        values: List<JsonElement>,
        classes: MutableMap<String, String>,
        usedClassNames: MutableSet<String>,
        missingInSomeObjects: Boolean = false
    ): String {
        val containsNull = values.any { it is JsonNull }
        val nonNullValues = values.filterNot { it is JsonNull }

        val baseType = when {
            nonNullValues.isEmpty() -> "Any?"

            nonNullValues.all { it is JsonObject } -> {
                generateClassFromObjects(
                    name.ifBlank { "NestedObject" },
                    nonNullValues.filterIsInstance<JsonObject>(),
                    classes,
                    usedClassNames
                )
            }

            nonNullValues.all { it is JsonArray } -> {
                val combinedArray = JsonArray(
                    nonNullValues
                        .filterIsInstance<JsonArray>()
                        .flatMap { it }
                )

                inferArrayType(
                    name,
                    combinedArray,
                    classes,
                    usedClassNames
                )
            }

            nonNullValues.all { it is JsonPrimitive } -> {
                inferPrimitiveTypes(
                    nonNullValues.filterIsInstance<JsonPrimitive>()
                )
            }

            else -> "Any?"
        }

        val nullable = containsNull || missingInSomeObjects

        return if (nullable && baseType != "Any?" &&
            !baseType.endsWith("?")
        ) {
            "$baseType?"
        } else {
            baseType
        }
    }

    private fun inferPrimitiveTypes(
        values: List<JsonPrimitive>
    ): String {
        val types = values.map { value ->
            when {
                value.isString -> "String"
                value.content == "true" ||
                        value.content == "false" -> "Boolean"
                value.content.toIntOrNull() != null -> "Int"
                value.content.toLongOrNull() != null -> "Long"
                value.content.toDoubleOrNull() != null -> "Double"
                else -> "Any?"
            }
        }.distinct()

        return when {
            types.size == 1 -> types.first()
            types.all { it == "Int" || it == "Long" } -> "Long"
            types.all {
                it == "Int" || it == "Long" || it == "Double"
            } -> "Double"
            else -> "Any?"
        }
    }

    private fun inferArrayType(
        name: String,
        array: JsonArray,
        classes: MutableMap<String, String>,
        usedClassNames: MutableSet<String>
    ): String {
        if (array.isEmpty()) return "List<Any?>"

        val nonNullValues = array.filterNot { it is JsonNull }
        val containsNull = nonNullValues.size < array.size

        if (nonNullValues.isEmpty()) {
            return "List<Any?>"
        }

        val elementType = if (nonNullValues.all { it is JsonObject }) {
            generateClassFromObjects(
                name,
                nonNullValues.filterIsInstance<JsonObject>(),
                classes,
                usedClassNames
            )
        } else {
            val types = nonNullValues.map { value ->
                inferMergedType(
                    name,
                    listOf(value),
                    classes,
                    usedClassNames
                )
            }.distinct()

            when {
                types.size == 1 -> types.first()
                types.all { it == "Int" || it == "Long" } -> "Long"
                types.all {
                    it == "Int" || it == "Long" || it == "Double"
                } -> "Double"
                else -> "Any?"
            }
        }

        val nullableElementType =
            if (containsNull && elementType != "Any?" &&
                !elementType.endsWith("?")
            ) {
                "$elementType?"
            } else {
                elementType
            }

        return "List<$nullableElementType>"
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
