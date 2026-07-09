package ru.v.fapc.tablehandler.domain.repository

import org.hibernate.validator.constraints.UUID
import ru.v.fapc.tablehandler.domain.TableAggregate

interface TableWriter {
    fun writeTable(tableAggregate: TableAggregate): UUID
}