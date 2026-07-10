package ru.v.fapc.tablehandler.domain

import ru.v.fapc.tablehandler.domain.exception.DomainValidationException

@JvmInline
value class Table private constructor(val table: List<List<String>>) {

    fun toCsv(lineSeparator: String = "\n", columnSeparator: String = ";") =
        table.joinToString(lineSeparator) { it.joinToString(columnSeparator) }

    companion object {
        fun fromString(string: String, lineSeparator: String = "\n", columnSeparator: String = ";"): Table {
            val rows = string.split(lineSeparator)
                .map { it.split(columnSeparator) }

            if (rows.size < 2 || rows.any { it.size < 2 }) {
                throw DomainValidationException(
                    "Table must have at least 2 rows and 2 columns",
                    listOf("rows=${rows.size}", "columns=${rows.firstOrNull()?.size ?: 0}")
                )
            }

            return Table(rows)
        }
    }
}
