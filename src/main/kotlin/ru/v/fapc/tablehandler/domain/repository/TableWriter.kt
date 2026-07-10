package ru.v.fapc.tablehandler.domain.repository

import ru.v.fapc.tablehandler.domain.TableAggregate

interface TableWriter {
    fun writeTable(tableAggregate: TableAggregate): TableAggregate
}