package ru.v.fapc.tablehandler.domain

import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import java.util.UUID

@ConsistentCopyVisibility
data class TableAggregate private constructor(
    val id: UUID?,
    val table: Table,
    val tableType: TableType,
    val tableSource: TableSource
) {
    companion object {
        fun create(saveCommandDto: SaveCommandDto): Result<TableAggregate> = runCatching {
            TableAggregate(
                id = null,
                table = Table.fromString(saveCommandDto.table),
                tableType = TableType.fromString(saveCommandDto.type),
                tableSource = TableSource.fromString(saveCommandDto.source)
            )
        }

        fun of(id: UUID, table: Table, tableType: TableType, tableSource: TableSource) =
            TableAggregate(id, table, tableType, tableSource)
    }

    fun withId(id: UUID) = copy(id = id)
}
