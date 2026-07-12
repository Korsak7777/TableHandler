package ru.v.fapc.tablehandler.presentation.rest

import com.example.model.SaveCommand
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ru.v.fapc.tablehandler.SoftAssertionsExtension
import ru.v.fapc.tablehandler.application.SaveTableUseCase
import ru.v.fapc.tablehandler.domain.Table
import ru.v.fapc.tablehandler.domain.TableAggregate
import ru.v.fapc.tablehandler.domain.TableSource
import ru.v.fapc.tablehandler.domain.TableType
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import tools.jackson.databind.json.JsonMapper
import java.util.UUID

@WebMvcTest(TableSaveControllerImpl::class)
@Import(GlobalExceptionHandler::class)
@ExtendWith(SoftAssertionsExtension::class)
class TableSaveControllerImplIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @MockitoBean
    private lateinit var saveTableUseCase: SaveTableUseCase

    private fun stubSaveUseCaseSuccess(tableType: String = "report", tableSource: String = "api"): UUID {
        val id = UUID.randomUUID()
        val aggregate = TableAggregate.of(
            id = id,
            table = Table.fromString("H1;H2\nV1;V2"),
            tableType = TableType.fromString(tableType),
            tableSource = TableSource.fromString(tableSource)
        )
        doReturn(Result.success(aggregate)).whenever(saveTableUseCase).saveTable(any<SaveCommandDto>())
        return id
    }

    @Test
    fun `saveTable should return SUCCESS ModelApiResponse with result id when saveCommand is valid`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf("type" to "report", "source" to "api"),
            table = "H1;H2\nV1;V2"
        )
        val expectedId = stubSaveUseCaseSuccess()

        // When
        val result = mockMvc.perform(
            post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(saveCommand))
        )

        // Then
        result
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(expectedId.toString()))
            .andExpect(jsonPath("$.error").doesNotExist())

        softly.assertThat(true).`as`("Request completed successfully").isTrue()
    }

    @Test
    fun `saveTable should return ERROR ModelApiResponse with 500 when use case throws RuntimeException`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf("type" to "report", "source" to "api"),
            table = "H1;H2\nV1;V2"
        )
        doReturn(Result.failure<TableAggregate>(RuntimeException("Database error")))
            .whenever(saveTableUseCase).saveTable(any<SaveCommandDto>())

        // When & Then
        mockMvc.perform(
            post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(saveCommand))
        )
            .andExpect(status().isInternalServerError)
            .andExpect(jsonPath("$.status").value("ERROR"))
            .andExpect(jsonPath("$.error.message").value("Database error"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    fun `saveTable should pass correct SaveCommand to use case`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf("type" to "summary", "source" to "import"),
            table = "A;B\nC;D"
        )
        stubSaveUseCaseSuccess("summary", "import")
        val commandCaptor = argumentCaptor<SaveCommandDto>()

        // When
        mockMvc.perform(
            post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(saveCommand))
        )
            .andExpect(status().isOk)

        // Then
        verify(saveTableUseCase).saveTable(commandCaptor.capture())
        val capturedCommand = commandCaptor.firstValue
        softly.assertThat(capturedCommand.type)
            .describedAs("Captured type").isEqualTo("summary")
        softly.assertThat(capturedCommand.source)
            .describedAs("Captured source").isEqualTo("import")
        softly.assertThat(capturedCommand.table)
            .describedAs("Captured table").isEqualTo("A;B\nC;D")
    }

    @Test
    fun `saveTable should return 400 when request body is empty`() {
        // When & Then
        mockMvc.perform(
            post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value("ERROR"))
            .andExpect(jsonPath("$.error.message").isNotEmpty)
    }

    @Test
    fun `saveTable should return 400 when request body is not valid JSON`() {
        // When & Then
        mockMvc.perform(
            post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not a json")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value("ERROR"))
            .andExpect(jsonPath("$.error.message").isNotEmpty)
    }

    @Test
    fun `saveTable should return 400 when metaData is null`() {
        // Given
        val invalidBody = """{"table": "H1;H2\nV1;V2"}"""

        // When & Then
        mockMvc.perform(
            post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody)
        )
            .andExpect(status().isBadRequest)
    }
}
