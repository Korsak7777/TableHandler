package ru.v.fapc.tablehandler

import com.example.model.ResultTableDto
import com.example.model.SaveCommand
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.RestTemplate
import ru.v.fapc.tablehandler.infrastructure.BaseDbIntegrationTest
import java.net.URI

@ExtendWith(SoftAssertionsExtension::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TableSaveControllerFullIntegrationTest: BaseDbIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    private val restTemplate = RestTemplate()

    private fun url(path: String) = URI.create("http://localhost:$port$path")

    @Test
    fun `saveTable should return 200 OK with ResultTableDto when saveCommand is valid`(softly: SoftAssertions) {
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

}