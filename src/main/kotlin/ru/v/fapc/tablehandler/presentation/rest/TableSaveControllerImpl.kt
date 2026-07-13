package ru.v.fapc.tablehandler.presentation.rest

import com.example.api.TableSaveControllerApi
import com.example.model.ApiError
import com.example.model.ModelApiResponse
import com.example.model.SaveCommand
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.v.fapc.tablehandler.application.SaveTableUseCase
import ru.v.fapc.tablehandler.domain.dto.SaveCommandDto
import ru.v.fapc.tablehandler.utils.getLogger

@RestController
class TableSaveControllerImpl(
    private val saveTableUseCase: SaveTableUseCase
): TableSaveControllerApi {

    private val log = getLogger()

    // TODO нужно глобальное логирование входящих и исходящих, включая трассировку
    override fun saveTable(saveCommand: SaveCommand): ResponseEntity<ModelApiResponse> {
        log.info("Saving tables: $saveCommand")

        val result = saveTableUseCase.saveTable(saveCommand.toDto())

        return result.fold(
            { dto ->
                log.info("Success save table. Result table id - ${dto.id}")
                ResponseEntity.ok(ModelApiResponse(
                    status = ModelApiResponse.Status.SUCCESS,
                    data = dto))
            },
            { ex ->
                log.error("Failed to save table", ex)
                ResponseEntity.internalServerError()
                    .body(ModelApiResponse(
                        status = ModelApiResponse.Status.ERROR,
                        error = ApiError(ex.message ?: "Unknown error while save table")))
            }
        )
    }

    private fun SaveCommand.toDto() =
        SaveCommandDto(
            this.metaData["type"],
            this.metaData["source"],
            this.table
        )

}
