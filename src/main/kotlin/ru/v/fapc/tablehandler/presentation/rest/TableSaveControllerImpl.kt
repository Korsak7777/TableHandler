package ru.v.fapc.tablehandler.presentation.rest

import com.example.api.TableSaveControllerApi
import com.example.model.ResultTableDto
import com.example.model.SaveCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.v.fapc.tablehandler.application.SaveTableUseCase
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import ru.v.fapc.tablehandler.utils.getLogger
import java.util.UUID

@RestController
open class TableSaveControllerImpl(
    private val saveTableUseCase: SaveTableUseCase
): TableSaveControllerApi {

    private val log = getLogger()

    // TODO нужно генерировать openApi doc из контроллеров
    // TODO нужен глобальный обработчик исключений через @RestControllerAdvice
    // TODO нужно глобальное логирование входящих и исходящих, включая трассировку
    override fun saveTable(saveCommand: SaveCommand): ResponseEntity<ResultTableDto> {
        log.info("Saving tables: $saveCommand")

        val res = saveTableUseCase.saveTable(saveCommand.toDto())

        log.info("Success sum tables. Result table id - $res")

        return ResponseEntity.ok().body(ResultTableDto(UUID.randomUUID()))
    }

    private fun SaveCommand.toDto() =
        SaveCommandDto(
            this.metaData["type"],
            this.metaData["source"],
            this.table
        )

}
