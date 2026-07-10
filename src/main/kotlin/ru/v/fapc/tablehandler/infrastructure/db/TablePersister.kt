package ru.v.fapc.tablehandler.infrastructure.db

import org.springframework.stereotype.Component
import ru.v.fapc.tablehandler.domain.Table
import ru.v.fapc.tablehandler.domain.TableAggregate
import ru.v.fapc.tablehandler.domain.TableSource
import ru.v.fapc.tablehandler.domain.TableType
import ru.v.fapc.tablehandler.domain.repository.TableWriter
import ru.v.fapc.tablehandler.utils.getLogger

@Component
class TablePersister(
    private val tableRepository: TableRepository
) : TableWriter {

    private val log = getLogger()

    override fun writeTable(tableAggregate: TableAggregate): TableAggregate {
        val entity = tableAggregate.toEntity()

        val saved = if (tableAggregate.id != null) {
            entity.id = tableAggregate.id
            tableRepository.save(entity)
        } else {
            tableRepository.save(entity)
        }

        log.info(
            "Table persisted successfully: id={}, tableType={}, tableSource={}, rows={}",
            saved.id,
            tableAggregate.tableType.value,
            tableAggregate.tableSource.value,
            tableAggregate.table.table.size
        )

        return tableAggregate.withId(saved.id!!)
    }

    private fun TableAggregate.toEntity() = TableEntity(
        id = this.id,
        tableData = this.table.toCsv(),
        tableType = this.tableType.value,
        tableSource = this.tableSource.value
    )

    private fun TableEntity.toAggregate() = TableAggregate.of(
        id = this.id!!,
        table = Table.fromString(this.tableData),
        tableType = TableType.fromString(this.tableType),
        tableSource = TableSource.fromString(this.tableSource)
    )
}
