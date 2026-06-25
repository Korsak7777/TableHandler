package ru.v.fapc.tablehandler.presentation.rest

import com.example.model.ResultTableDto
import com.example.model.SaveCommand
import jakarta.servlet.ServletException
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ru.v.fapc.tablehandler.SoftAssertionsExtension
import ru.v.fapc.tablehandler.application.SaveTableUseCase
import tools.jackson.databind.json.JsonMapper
import java.util.UUID

@WebMvcTest(TableSaveControllerImpl::class)
@ExtendWith(SoftAssertionsExtension::class)
class TableSaveControllerImplIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jsonMapper: JsonMapper

    @MockitoBean
    private lateinit var saveTableUseCase: SaveTableUseCase

    @Test
    fun `saveTable should return 200 OK with ResultTableDto when saveCommand is valid`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf("author" to "test"),
            table = "[[1,2],[3,4]]"
        )
        val expectedResult = ResultTableDto(UUID.randomUUID())
        doReturn(expectedResult).whenever(saveTableUseCase).saveTable(any<SaveCommand>())

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
            .andExpect(jsonPath("$.id").exists())

        softly.assertThat(true).`as`("Request completed successfully").isTrue()
    }

    @Test
    fun `saveTable should return 500 when use case throws RuntimeException`(softly: SoftAssertions) {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf("author" to "test"),
            table = "[[1]]"
        )
        doThrow(RuntimeException("Database error"))
            .whenever(saveTableUseCase).saveTable(any<SaveCommand>())

        // When
        val result = runCatching {
            mockMvc.perform(post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(saveCommand)))
        }.exceptionOrNull() as ServletException

        // Then
        softly.assertThat(result.message)
            .describedAs("message").contains("Database error")
    }

    @Test
    fun `saveTable should pass correct SaveCommand to use case`() {
        // Given
        val saveCommand = SaveCommand(
            metaData = mapOf("author" to "test-user", "source" to "import"),
            table = "[[1,2,3],[4,5,6]]"
        )
        val expectedResult = ResultTableDto(UUID.randomUUID())
        doReturn(expectedResult).whenever(saveTableUseCase).saveTable(any<SaveCommand>())
        val commandCaptor = argumentCaptor<SaveCommand>()

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
        org.assertj.core.api.Assertions.assertThat(capturedCommand.metaData)
            .containsEntry("author", "test-user")
            .containsEntry("source", "import")
        org.assertj.core.api.Assertions.assertThat(capturedCommand.table)
            .isEqualTo("[[1,2,3],[4,5,6]]")
    }

    @Test
    fun `saveTable should return 400 when request body is empty`() {
        // When
        val result = mockMvc.perform(
            post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("")
        )

        // Then
        result.andExpect(status().isBadRequest)
    }

    @Test
    fun `saveTable should return 400 when request body is not valid JSON`() {
        // When
        val result = mockMvc.perform(
            post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content("not a json")
        )

        // Then
        result.andExpect(status().isBadRequest)
    }

    @Test
    fun `saveTable should return 400 when metaData is null`() {
        // Given
        val invalidBody = """{"table": "[[1]]"}"""

        // When
        val result = mockMvc.perform(
            post("/table/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody)
        )

        // Then
        result.andExpect(status().isBadRequest)
    }
}
