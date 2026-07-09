package ru.v.fapc.tablehandler.domain

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.v.fapc.tablehandler.domain.exception.DomainValidationException

@ExtendWith(SoftAssertionsExtension::class)
internal class TableTypeTest {

    @Test
    fun `fromString returns TableType for valid non-blank string`(softly: SoftAssertions) {
        val result = TableType.fromString("report")

        softly.assertThat(result.value).isEqualTo("report")
    }

    @Test
    fun `fromString throws DomainValidationException for null`() {
        assertThatThrownBy { TableType.fromString(null) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("TableType")
    }

    @Test
    fun `fromString throws DomainValidationException for blank string`() {
        assertThatThrownBy { TableType.fromString("   ") }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("TableType")
    }
}
