package ru.v.fapc.tablehandler.domain.exception

class DomainValidationException : RuntimeException {
    val details: List<String>

    constructor(message: String) : super(message) {
        this.details = listOf()
    }

    constructor(message: String, details: List<String>) : super(message) {
        this.details = details.toList()
    }
}