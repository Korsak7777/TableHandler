package ru.v.fapc.tablehandler.presentation.rest

import com.example.api.TableReadControllerApi
import com.example.model.TableDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.v.fapc.tablehandler.application.ReadTableUseCase
import ru.v.fapc.tablehandler.utils.getLogger
import java.util.UUID

@RestController
class TableReadControllerImpl(
    private val readTableUseCase: ReadTableUseCase
) : TableReadControllerApi {

    private val log = getLogger()

    override fun readTable(id: UUID): ResponseEntity<TableDto> {
        log.info("Reading table by id: $id")

        val result = readTableUseCase.readTable(id)
            ?: return ResponseEntity.notFound().build()

        log.info("Table retrieved successfully: id=${result.id}")

        return ResponseEntity.ok().body(
            TableDto(
                id = result.id,
                tableType = result.tableType,
                tableSource = result.tableSource,
                table = result.table
            )
        )
    }
}
