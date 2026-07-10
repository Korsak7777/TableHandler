package ru.v.fapc.tablehandler.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.v.fapc.tablehandler.domain.TableAggregate
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import ru.v.fapc.tablehandler.domain.repository.TableWriter

@Service
class SaveTableUseCase(
    private val tableWriter: TableWriter
) {
    // 1. TODO достать таблицу на уровне контроллера и поместить в VO таблицы. Проверки на минимальную высоту и ширину
    // 2. TODO на основе VO таблицы сохранить таблицу и метаданные (тип, источник)
    // 3. TODO вернуть id сохраненной таблицы
    @Transactional
    fun saveTable(saveCommandDto: SaveCommandDto) =
        TableAggregate.create(saveCommandDto)
            .map { tableWriter.writeTable(it) }
            .map { it }
}