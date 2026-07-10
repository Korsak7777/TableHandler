package ru.v.fapc.tablehandler.domain

import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import java.util.UUID

@ConsistentCopyVisibility
data class TableAggregate private constructor(
    private val id: UUID?,
    private val table: Table,
    private val tableType: TableType,
    private val tableSource: TableSource
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
    }
}
