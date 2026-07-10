package ru.v.fapc.tablehandler.domain

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import ru.v.fapc.tablehandler.domain.exception.DomainValidationException

@ExtendWith(SoftAssertionsExtension::class)
internal class TableAggregateTest {

    private val validDto = SaveCommandDto(
        type = "report",
        source = "api",
        table = "Header1;Header2\nValue1;Value2"
    )

    @Test
    fun `create returns TableAggregate for valid DTO`(softly: SoftAssertions) {
        val result = TableAggregate.create(validDto)

        softly.assertThat(result.isSuccess).isTrue()
        result.onSuccess { aggregate ->
            softly.assertThat(aggregate).isNotNull()
        }
    }

    @Test
    fun `create fails when type is null`() {
        val dto = validDto.copy(type = null)

        val result = TableAggregate.create(dto)

        assertThatThrownBy { result.getOrThrow() }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("TableType")
    }

    @Test
    fun `create fails when source is null`() {
        val dto = validDto.copy(source = null)

        val result = TableAggregate.create(dto)

        assertThatThrownBy { result.getOrThrow() }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("TableSource")
    }

    @Test
    fun `create fails when table has single row`() {
        val dto = validDto.copy(table = "Only;Row")

        val result = TableAggregate.create(dto)

        assertThatThrownBy { result.getOrThrow() }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("2 rows")
    }
}
