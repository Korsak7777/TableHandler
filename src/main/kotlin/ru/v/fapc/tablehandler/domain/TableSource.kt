package ru.v.fapc.tablehandler.domain

import ru.v.fapc.tablehandler.domain.exception.DomainValidationException

@JvmInline
value class TableSource private constructor(val value: String) {

    companion object {
        fun fromString(string: String?): TableSource =
            if (string.isNullOrBlank())
                throw DomainValidationException("TableSource must not be null or blank")
            else
                TableSource(string)
    }

}
