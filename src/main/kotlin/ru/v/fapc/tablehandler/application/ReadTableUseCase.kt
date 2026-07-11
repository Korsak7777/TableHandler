package ru.v.fapc.tablehandler.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.v.fapc.tablehandler.domain.dto.TableDto
import ru.v.fapc.tablehandler.domain.repository.TableReader
import java.util.UUID

@Service
class ReadTableUseCase(
    private val tableReader: TableReader
) {

    @Transactional(readOnly = true)
    fun readTable(id: UUID): TableDto? =
        tableReader.findById(id)?.let {
            TableDto(
                id = it.id!!,
                tableType = it.tableType.value,
                tableSource = it.tableSource.value,
                table = it.table.toCsv()
            )
        }
}
