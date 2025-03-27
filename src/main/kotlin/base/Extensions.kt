package com.alpenraum.base

import org.slf4j.LoggerFactory
import java.util.*

fun <T> getLogger(clazz: Class<T>) = LoggerFactory.getLogger(clazz)

fun String.isValidUUID(): Boolean {
    return try {
        UUID.fromString(this)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}
