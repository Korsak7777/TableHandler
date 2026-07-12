package ru.v.fapc.tablehandler.domain.dto

import java.util.UUID

data class TableDto(
    val id: UUID,
    val tableType: String,
    val tableSource: String,
    val table: String
)
