package ru.v.fapc.tablehandler.domain.dto

data class SaveCommandDto(
    val type: String?,
    val source: String?,
    val table: String,
)