package ru.v.fapc.tablehandler

import com.example.model.ModelApiResponse
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import ru.v.fapc.tablehandler.domain.TableAggregate
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import ru.v.fapc.tablehandler.infrastructure.BaseDbIntegrationTest
import ru.v.fapc.tablehandler.infrastructure.db.TablePersister
import tools.jackson.databind.json.JsonMapper
import java.net.URI
import java.util.*

@ExtendWith(SoftAssertionsExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TableReadControllerFullIntegrationTest : BaseDbIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var tablePersister: TablePersister

    private val restTemplate = RestTemplate()
    private val jsonMapper = JsonMapper()

    private fun url(path: String) = URI.create("http://localhost:$port$path")

    @Test
    fun `readTable should return SUCCESS ModelApiResponse with table data when table exists`(softly: SoftAssertions) {
        // Given
        val dto = SaveCommandDto(type = "report", source = "api", table = "Header1;Header2\nValue1;Value2")
        val aggregate = TableAggregate.create(dto).getOrThrow()
        val saved = tablePersister.writeTable(aggregate)

        // When
        val response = restTemplate.getForEntity(
            url("/table/${saved.id}"), ModelApiResponse::class.java
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
            .describedAs("Table id").isEqualTo(saved.id.toString())
        softly.assertThat(data?.get("tableType"))
            .describedAs("Table type").isEqualTo("report")
        softly.assertThat(data?.get("tableSource"))
            .describedAs("Table source").isEqualTo("api")
        softly.assertThat(data?.get("table"))
            .describedAs("Table data").isEqualTo("Header1;Header2\nValue1;Value2")
    }

    @Test
    fun `readTable should return ERROR ModelApiResponse when table does not exist`(softly: SoftAssertions) {
        // Given
        val nonExistentId = UUID.randomUUID()

        // When
        val ex = org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException::class.java) {
            restTemplate.getForEntity(
                url("/table/$nonExistentId"), ModelApiResponse::class.java
            )
        }

        // Then
        softly.assertThat(ex.statusCode)
            .describedAs("HTTP status").isEqualTo(HttpStatus.NOT_FOUND)

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
}
