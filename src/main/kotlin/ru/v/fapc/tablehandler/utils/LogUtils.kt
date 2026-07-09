package ru.v.fapc.tablehandler.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T: Any> T.getLogger(): Logger =
    LoggerFactory.getLogger(this::class.java.name)
