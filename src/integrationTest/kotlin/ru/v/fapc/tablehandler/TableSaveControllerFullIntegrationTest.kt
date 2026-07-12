package ru.v.fapc.tablehandler

import com.example.model.ModelApiResponse
import com.example.model.SaveCommand
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import ru.v.fapc.tablehandler.infrastructure.BaseDbIntegrationTest
import tools.jackson.databind.json.JsonMapper
import java.net.URI

@ExtendWith(SoftAssertionsExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TableSaveControllerFullIntegrationTest : BaseDbIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    private val restTemplate = RestTemplate()
    private val jsonMapper = JsonMapper()

    private fun url(path: String) = URI.create("http://localhost:$port$path")

    @Test
    fun `saveTable should return SUCCESS ModelApiResponse with result id when saveCommand is valid`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf(
                "type" to "report",
                "source" to "api"
            ),
            table = "Header1;Header2\nValue1;Value2"
        )

        // When
        val response = restTemplate.postForEntity(
            url("/table/save"), saveCommand, ModelApiResponse::class.java
        )

        // Then
        softly.assertThat(response.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.OK)
        softly.assertThat(response.body)
            .describedAs("Response body").isNotNull
        softly.assertThat(response.body?.status)
            .describedAs("Response status").isEqualTo(ModelApiResponse.Status.SUCCESS)
        softly.assertThat(response.body?.error)
            .describedAs("Error should be null on success").isNull()

        val data = response.body?.data as? Map<*, *>
        softly.assertThat(data)
            .describedAs("Data payload").isNotNull
        softly.assertThat(data?.get("id"))
            .describedAs("Result table id").isNotNull
    }

    @Test
    fun `saveTable should return ERROR ModelApiResponse with 500 when table data is empty`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf(
                "type" to "report",
                "source" to "api"
            ),
            table = ""
        )

        // When
        val ex = assertThrows(HttpServerErrorException::class.java) {
            restTemplate.postForEntity(
                url("/table/save"), saveCommand, ModelApiResponse::class.java
            )
        }

        // Then
        softly.assertThat(ex.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)

        val body = ex.responseBodyAsString
        val apiResponse = jsonMapper.readValue(body, ModelApiResponse::class.java)
        softly.assertThat(apiResponse.status)
            .describedAs("Response status").isEqualTo(ModelApiResponse.Status.ERROR)
        softly.assertThat(apiResponse.error)
            .describedAs("Error details").isNotNull
        softly.assertThat(apiResponse.error?.message)
            .describedAs("Error message").isNotBlank
        softly.assertThat(apiResponse.data)
            .describedAs("Data should be null on error").isNull()
    }

    @Test
    fun `saveTable should return ERROR ModelApiResponse with 500 when table has insufficient rows`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf(
                "type" to "report",
                "source" to "api"
            ),
            table = "OnlyHeader"
        )

        // When
        val ex = assertThrows(HttpServerErrorException::class.java) {
            restTemplate.postForEntity(
                url("/table/save"), saveCommand, ModelApiResponse::class.java
            )
        }

        // Then
        softly.assertThat(ex.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)

        val body = ex.responseBodyAsString
        val apiResponse = jsonMapper.readValue(body, ModelApiResponse::class.java)
        softly.assertThat(apiResponse.status)
            .describedAs("Response status").isEqualTo(ModelApiResponse.Status.ERROR)
        softly.assertThat(apiResponse.error?.message)
            .describedAs("Error message").isNotBlank
        softly.assertThat(apiResponse.error?.details)
            .describedAs("Error details list").isNotNull
    }
}
