package ru.v.fapc.tablehandler.presentation.rest

import com.example.model.ResultTableDto
import com.example.model.SaveCommand
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import ru.v.fapc.tablehandler.SoftAssertionsExtension
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SoftAssertionsExtension::class)
class TableSaveControllerFullIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    private val restTemplate = RestTemplate()

    private fun url(path: String) = URI.create("http://localhost:$port$path")

    @Test
    fun `saveTable should return 200 OK with ResultTableDto when saveCommand is valid`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf("author" to "integration-test"),
            table = "[[1,2],[3,4]]"
        )

        // When
        val response = restTemplate.postForEntity(
            url("/table/save"), saveCommand, ResultTableDto::class.java
        )

        // Then
        softly.assertThat(response.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.OK)
        softly.assertThat(response.body)
            .describedAs("Response body").isNotNull
        softly.assertThat(response.body?.id)
            .describedAs("Result id").isNotNull
    }

    @Test
    fun `saveTable should return 200 with large metadata map`(softly: SoftAssertions) {
        // Given
        val largeMetaData = (1..100).associate { "key-$it" to "value-$it" }
        val saveCommand = SaveCommand(
            metaData = largeMetaData,
            table = "[[1]]"
        )

        // When
        val response = restTemplate.postForEntity(
            url("/table/save"), saveCommand, ResultTableDto::class.java
        )

        // Then
        softly.assertThat(response.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.OK)
        softly.assertThat(response.body?.id)
            .describedAs("Result id").isNotNull
    }

    @Test
    fun `saveTable should return 200 with empty metadata map`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = emptyMap(),
            table = "[[1,2],[3,4]]"
        )

        // When
        val response = restTemplate.postForEntity(
            url("/table/save"), saveCommand, ResultTableDto::class.java
        )

        // Then
        softly.assertThat(response.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.OK)
        softly.assertThat(response.body?.id)
            .describedAs("Result id").isNotNull
    }

    @Test
    fun `saveTable should return 400 when request body has missing required fields`(softly: SoftAssertions) {
        // Given
        val invalidBody = mapOf("invalid" to "data")

        // When
        val response = runCatching {
            restTemplate.postForEntity(
                url("/table/save"), invalidBody, String::class.java
            )
        }.exceptionOrNull() as HttpClientErrorException.BadRequest

        // Then
        softly.assertThat(response.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `saveTable should return 400 when metaData field is missing`(softly: SoftAssertions) {
        // Given
        val invalidBody = mapOf("table" to "[[1]]")

        // When
        val response = runCatching {
            restTemplate.postForEntity(
                url("/table/save"), invalidBody, String::class.java
            )
        }.exceptionOrNull() as HttpClientErrorException.BadRequest

        // Then
        softly.assertThat(response.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `saveTable should return 400 when table field is missing`(softly: SoftAssertions) {
        // Given
        val invalidBody = mapOf("metaData" to mapOf("author" to "test"))

        // When
        val response = runCatching {
            restTemplate.postForEntity(
                url("/table/save"), invalidBody, String::class.java
            )
        }.exceptionOrNull() as HttpClientErrorException.BadRequest

        // Then
        softly.assertThat(response.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `saveTable should return 415 when content type is not JSON`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf("author" to "test"),
            table = "[[1]]"
        )

        // When
        val response = restTemplate.postForEntity(
            url("/table/save"), saveCommand, String::class.java
        )

        // Then
        softly.assertThat(response.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `saveTable response should contain id with valid UUID format`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf("author" to "test"),
            table = "[[1,2],[3,4]]"
        )

        // When
        val response = restTemplate.postForEntity(
            url("/table/save"), saveCommand, ResultTableDto::class.java
        )

        // Then
        softly.assertThat(response.body)
            .describedAs("Response body").isNotNull
        response.body?.let { result ->
            softly.assertThat(result.id)
                .describedAs("Result id").isNotNull
            softly.assertThat(result.id.version())
                .describedAs("UUID version").isEqualTo(4)
            softly.assertThat(result.id.toString())
                .describedAs("UUID format")
                .matches("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")
        }
    }
}
