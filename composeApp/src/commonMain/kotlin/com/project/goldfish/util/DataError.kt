package com.project.goldfish.util

sealed interface DataError: Error {
    enum class Network: DataError {
        CONFLICT,
        BAD_REQUEST,
        FORBIDDEN,
        METHOD_NOT_ALLOWED,
        UNAUTHORIZED,
        SERVER_ERROR,
        UNKNOWN
    }
    enum class Local: DataError {
        DISK_FULL
    }
}