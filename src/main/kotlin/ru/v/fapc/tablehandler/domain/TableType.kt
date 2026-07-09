package ru.v.fapc.tablehandler.domain

import ru.v.fapc.tablehandler.domain.exception.DomainValidationException

@JvmInline
value class TableType private constructor(val value: String) {

    companion object {
        fun fromString(string: String?): TableType =
            if (string.isNullOrBlank())
                throw DomainValidationException("TableType must not be null or blank")
            else
                TableType(string)
    }

}
