package ru.v.fapc.tablehandler.presentation.rest

import com.example.model.ResultTableDto
import com.example.model.SaveCommand
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.v.fapc.tablehandler.SoftAssertionsExtension
import ru.v.fapc.tablehandler.application.SaveTableUseCase
import ru.v.fapc.tablehandler.domain.TableAggregate
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import java.net.URI
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ExtendWith(SoftAssertionsExtension::class)
class TableSaveControllerFullIntegrationTest {

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
        }
    }

    @LocalServerPort
    private var port: Int = 0

    private val restTemplate = RestTemplate()

    @MockitoBean
    private lateinit var saveTableUseCase: SaveTableUseCase

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
        val aggregate = TableAggregate.create(SaveCommandDto("report", "api", "Header1;Header2\nValue1;Value2")).getOrThrow()
        // TODO убрать после реализации TableWriter
        whenever(saveTableUseCase.saveTable(any<SaveCommandDto>())).thenReturn(Result.success(aggregate))

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
