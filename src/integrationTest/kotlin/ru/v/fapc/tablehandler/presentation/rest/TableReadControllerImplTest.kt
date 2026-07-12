package ru.v.fapc.tablehandler.presentation.rest

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ru.v.fapc.tablehandler.SoftAssertionsExtension
import ru.v.fapc.tablehandler.application.ReadTableUseCase
import ru.v.fapc.tablehandler.domain.dto.TableDto
import java.util.UUID

@WebMvcTest(TableReadControllerImpl::class)
@Import(GlobalExceptionHandler::class)
@ExtendWith(SoftAssertionsExtension::class)
class TableReadControllerImplTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var readTableUseCase: ReadTableUseCase

    @Test
    fun `readTable should return SUCCESS ModelApiResponse with table data when table exists`(softly: SoftAssertions) {
        // Given
        val tableId = UUID.randomUUID()
        val tableDto = TableDto(
            id = tableId,
            tableType = "report",
            tableSource = "api",
            table = "H1;H2\nV1;V2"
        )
        doReturn(tableDto).whenever(readTableUseCase).readTable(tableId)

        // When
        val result = mockMvc.perform(
            get("/table/{id}", tableId)
                .accept(MediaType.APPLICATION_JSON)
        )

        // Then
        result
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(tableId.toString()))
            .andExpect(jsonPath("$.data.tableType").value("report"))
            .andExpect(jsonPath("$.data.tableSource").value("api"))
            .andExpect(jsonPath("$.data.table").value("H1;H2\nV1;V2"))
            .andExpect(jsonPath("$.error").doesNotExist())

        softly.assertThat(true).`as`("Request completed successfully").isTrue()
    }

    @Test
    fun `readTable should return ERROR ModelApiResponse with 404 when table does not exist`(softly: SoftAssertions) {
        // Given
        val tableId = UUID.randomUUID()
        doReturn(null).whenever(readTableUseCase).readTable(tableId)

        // When
        val result = mockMvc.perform(
            get("/table/{id}", tableId)
                .accept(MediaType.APPLICATION_JSON)
        )

        // Then
        result
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value("ERROR"))
            .andExpect(jsonPath("$.error.message").isNotEmpty)
            .andExpect(jsonPath("$.error.details").isArray)
            .andExpect(jsonPath("$.data").doesNotExist())

        softly.assertThat(true).`as`("Request completed with 404").isTrue()
    }

    @Test
    fun `readTable should pass correct id to use case`(softly: SoftAssertions) {
        // Given
        val tableId = UUID.randomUUID()
        val tableDto = TableDto(
            id = tableId,
            tableType = "summary",
            tableSource = "manual",
            table = "A;B\nC;D"
        )
        doReturn(tableDto).whenever(readTableUseCase).readTable(tableId)

        // When
        mockMvc.perform(
            get("/table/{id}", tableId)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)

        // Then
        verify(readTableUseCase).readTable(tableId)
    }
}
