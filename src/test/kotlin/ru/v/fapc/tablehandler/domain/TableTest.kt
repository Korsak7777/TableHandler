package ru.v.fapc.tablehandler.domain

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ru.v.fapc.tablehandler.domain.exception.DomainValidationException

@ExtendWith(SoftAssertionsExtension::class)
internal class TableTest {

    @Test
    fun `fromString returns Table for valid CSV 2x2`(softly: SoftAssertions) {
        val csv = "Header1;Header2\nValue1;Value2"

        val result = Table.fromString(csv)

        softly.assertThat(result.table).hasSize(2)
        softly.assertThat(result.table[0]).containsExactly("Header1", "Header2")
        softly.assertThat(result.table[1]).containsExactly("Value1", "Value2")
    }

    @Test
    fun `fromString returns Table for valid CSV 3x3`(softly: SoftAssertions) {
        val csv = "A;B;C\n1;2;3\nX;Y;Z"

        val result = Table.fromString(csv)

        softly.assertThat(result.table).hasSize(3)
        softly.assertThat(result.table[0]).containsExactly("A", "B", "C")
        softly.assertThat(result.table[2]).containsExactly("X", "Y", "Z")
    }

    @Test
    fun `fromString throws DomainValidationException for single row`() {
        val csv = "Only;Header;Row"

        assertThatThrownBy { Table.fromString(csv) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("2 rows")
    }

    @Test
    fun `fromString throws DomainValidationException for single column`() {
        val csv = "Header\nValue"

        assertThatThrownBy { Table.fromString(csv) }
            .isInstanceOf(DomainValidationException::class.java)
            .hasMessageContaining("2 columns")
    }

    @Test
    fun `fromString uses custom separators`(softly: SoftAssertions) {
        val csv = "Name|Age\nAlice|30"

        val result = Table.fromString(csv, columnSeparator = "|")

        softly.assertThat(result.table).hasSize(2)
        softly.assertThat(result.table[1]).containsExactly("Alice", "30")
    }

    @Test
    fun `toCsv produces correct CSV string`(softly: SoftAssertions) {
        val csv = "A;B\n1;2"
        val table = Table.fromString(csv)

        val result = table.toCsv()

        softly.assertThat(result).isEqualTo("A;B\n1;2")
    }
}
