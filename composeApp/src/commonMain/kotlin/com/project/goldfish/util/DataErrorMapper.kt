package com.project.goldfish.util

import com.project.goldfish.resources.Res
import com.project.goldfish.resources.server_error
import com.project.goldfish.resources.unknown_error
import org.jetbrains.compose.resources.getString

suspend fun DataError.asString(): String {
    return when (this) {
        DataError.Network.SERVER_ERROR -> getString(
            Res.string.server_error
        )
        DataError.Network.UNKNOWN -> getString(
            Res.string.unknown_error
        )
        DataError.Network.CONFLICT -> getString(
            Res.string.unknown_error
        )
        DataError.Network.BAD_REQUEST -> getString(
            Res.string.unknown_error
        )
        DataError.Network.FORBIDDEN -> getString(
            Res.string.unknown_error
        )
        DataError.Network.METHOD_NOT_ALLOWED -> getString(
            Res.string.unknown_error
        )
        DataError.Network.UNAUTHORIZED -> getString(
            Res.string.unknown_error
        )
        DataError.Local.DISK_FULL -> getString(
            Res.string.unknown_error
        )
    }
}