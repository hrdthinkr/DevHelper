package io.github.hrdthinkr.devhelper

import io.github.hrdthinkr.devhelper.data.repository.JsonToKotlinGeneratorImpl
import junit.framework.TestCase.assertTrue
import org.junit.Test

class JsonToKotlinGeneratorTest {

    @Test
    fun generate_simplejson_returnsKotlinProperties() {
        val generator = JsonToKotlinGeneratorImpl()

        val json = """{"name": "Alex", "age": 20}"""

        val result = generator.generate(json, "User")

        assertTrue(result.contains("data class User("))
        assertTrue(result.contains("val name: String"))
        assertTrue(result.contains("val age: Int"))
    }

    @Test
    fun generate_nestedObject_createsSeparateDataClasses() {
        val generator = JsonToKotlinGeneratorImpl()

        val json = """
        {
            "name": "Alex",
            "address": {
                "city": "Novosibirsk"
            }
        }
    """.trimIndent()

        val result = generator.generate(json, "User")

        assertTrue(result.contains("data class User("))
        assertTrue(result.contains("data class Address("))
        assertTrue(result.contains("val address: Address"))
        assertTrue(result.contains("val city: String"))
    }

    @Test
    fun generate_arrayOfObjects_createsDataClass() {
        val generator = JsonToKotlinGeneratorImpl()

        val json = """
        {
            "users": [
                {
                    "name": "Alex",
                    "age": 20
                },
                {
                    "name": "Kate",
                    "age": 21
                }
            ]
        }
    """.trimIndent()

        val result = generator.generate(json, "Response")

        assertTrue(result.contains("data class Response("))
        assertTrue(result.contains("data class Users("))
        assertTrue(result.contains("val users: List<Users>"))
        assertTrue(result.contains("val name: String"))
        assertTrue(result.contains("val age: Int"))
    }

    @Test
    fun generate_invalidJson_throwsException() {
        val generator = JsonToKotlinGeneratorImpl()

        val json = """{"name": }"""

        try {
            generator.generate(json, "User")
            throw AssertionError("Expected IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertTrue(exception.message == "Invalid JSON")
        }
    }

    @Test
    fun generate_sameNestedObjectNames_createsDifferentClasses() {
        val generator = JsonToKotlinGeneratorImpl()

        val json = """
        {
            "user": {
                "name": "Alex"
            },
            "admin": {
                "role": "admin"
            }
        }
    """.trimIndent()

        val result = generator.generate(json, "Response")

        assertTrue(result.contains("data class Response("))
        assertTrue(result.contains("data class User("))
        assertTrue(result.contains("data class Admin("))
        assertTrue(result.contains("val user: User"))
        assertTrue(result.contains("val admin: Admin"))
        assertTrue(result.contains("val name: String"))
        assertTrue(result.contains("val role: String"))
    }

    @Test
    fun generate_sameNestedClassNames_doesNotLoseFields() {
        val generator = JsonToKotlinGeneratorImpl()

        val json = """
        {
            "user": {
                "profile": {
                    "name": "Alex"
                }
            },
            "admin": {
                "profile": {
                    "role": "admin"
                }
            }
        }
    """.trimIndent()

        val result = generator.generate(json, "Response")

        assertTrue(result.contains("data class Response("))
        assertTrue(result.contains("data class User("))
        assertTrue(result.contains("data class Admin("))
        assertTrue(result.contains("val name: String"))
        assertTrue(result.contains("val role: String"))
    }

    @Test
    fun generate_arrayOfObjectsWithDifferentFields_makesMissingFieldsNullable() {
        val generator = JsonToKotlinGeneratorImpl()

        val json = """
        {
            "users": [
                {
                    "name": "Alex",
                    "age": 20
                },
                {
                    "name": "Kate",
                    "city": "Moscow"
                }
            ]
        }
    """.trimIndent()

        val result = generator.generate(json, "Response")

        assertTrue(result.contains("val name: String"))
        assertTrue(result.contains("val age: Int?"))
        assertTrue(result.contains("val city: String?"))
    }

    @Test
    fun generate_arrayOfObjectsWithNullValue_makesPropertyNullable() {
        val generator = JsonToKotlinGeneratorImpl()

        val json = """
        {
            "users": [
                {
                    "age": 20
                },
                {
                    "age": null
                }
            ]
        }
    """.trimIndent()

        val result = generator.generate(json, "Response")

        assertTrue(result.contains("val age: Int?"))
    }

}
