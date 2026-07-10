package ru.v.fapc.tablehandler.domain

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.v.fapc.tablehandler.domain.exception.DomainValidationException

@ExtendWith(SoftAssertionsExtension::class)
internal class TableSourceTest {

    @Test
    fun `fromString returns TableSource for valid non-blank string`(softly: SoftAssertions) {
        val result = TableSource.fromString("api")

        softly.assertThat(result.value).isEqualTo("api")
    }

    @Test
    fun `fromString throws DomainValidationException for null`() {
        assertThatThrownBy { TableSource.fromString(null) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("TableSource")
    }

    @Test
    fun `fromString throws DomainValidationException for blank string`() {
        assertThatThrownBy { TableSource.fromString("   ") }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("TableSource")
    }
}
