package io.github.hrdthinkr.devhelper.domain.generator

interface JsonToKotlinGenerator {
    fun generate(
        json: String,
        className: String
    ): String
}
