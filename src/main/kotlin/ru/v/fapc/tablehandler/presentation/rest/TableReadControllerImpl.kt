package ru.v.fapc.tablehandler.presentation.rest

import com.example.api.TableReadControllerApi
import com.example.model.ApiError
import com.example.model.ModelApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.v.fapc.tablehandler.application.ReadTableUseCase
import ru.v.fapc.tablehandler.utils.getLogger
import java.util.*

@RestController
class TableReadControllerImpl(
    private val readTableUseCase: ReadTableUseCase
) : TableReadControllerApi {

    private val log = getLogger()

    override fun readTable(id: UUID): ResponseEntity<ModelApiResponse> {
        log.info("Reading table by id: $id")

        val result = readTableUseCase.readTable(id)

        if (result == null){
            return ResponseEntity.status(404)
                .body(ModelApiResponse(
                    status = ModelApiResponse.Status.ERROR,
                    error = ApiError("Table not found", listOf("Table with id=$id does not exist"))))
        } else {
            log.info("Table retrieved successfully: id=${result.id}")
            return ResponseEntity.ok(ModelApiResponse(
                status = ModelApiResponse.Status.SUCCESS,
                data = result))
        }
    }
}
