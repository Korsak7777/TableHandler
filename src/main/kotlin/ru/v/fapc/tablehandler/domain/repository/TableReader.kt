package ru.v.fapc.tablehandler.domain.repository

import ru.v.fapc.tablehandler.domain.TableAggregate
import java.util.UUID

interface TableReader {
    fun findById(id: UUID): TableAggregate?
}
