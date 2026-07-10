package ru.v.fapc.tablehandler.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.v.fapc.tablehandler.domain.TableAggregate
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import ru.v.fapc.tablehandler.domain.repository.TableWriter

@Service
open class SaveTableUseCase(
    private val tableWriter: TableWriter
) {

    @Transactional
    fun saveTable(saveCommandDto: SaveCommandDto) =
        TableAggregate.create(saveCommandDto)
            .map { tableWriter.writeTable(it) }
            .map { it }
}