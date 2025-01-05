package com.project.goldfish.util

typealias RootError = Error

sealed interface GFResult<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): GFResult<D, E>
    data class Error<out D, out E: RootError>(val error: E): GFResult<D, E>
}