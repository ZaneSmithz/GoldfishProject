package com.project.goldfish.network.util

import com.project.goldfish.logEvent
import com.project.goldfish.util.DataError
import com.project.goldfish.util.GFResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

suspend inline fun <reified T>  HttpClient.executePostForResponseBody(
    endpoint: String,
    formDataContent: FormDataContent?,
    firebaseToken: String,
): GFResult<T, DataError.Network> {
    return try {
        GFResult.Success(this.post(endpoint) {
            header("Authorization", "Bearer $firebaseToken")
            contentType(ContentType.Application.Json)
            setBody(formDataContent)
        }.body<T>())
    }
    catch (e: ClientRequestException) {
        e.printStackTrace()
        GFResult.Error(statusCodeMapper(e.response.status))
    }
    catch (e: Exception) {
        e.printStackTrace()
        logEvent("EXCEPTION = ${e.message}")
        GFResult.Error(DataError.Network.UNKNOWN)
    }
}

suspend inline fun <reified T> HttpClient.executeGetForResponseBody(
    endpoint: String,
    firebaseUid: String,
): GFResult<T, DataError.Network> {
    return try {
        GFResult.Success(this.get(endpoint) {
            header("Authorization", "Bearer $firebaseUid")
            contentType(ContentType.Application.Json)
        }.body<T>())
    }
    catch (e: ClientRequestException) {
        e.printStackTrace()
        GFResult.Error(statusCodeMapper(e.response.status))
    }
    catch (e: Exception) {
        e.printStackTrace()
        GFResult.Error(DataError.Network.UNKNOWN)
    }
}

fun statusCodeMapper(statusCode: HttpStatusCode): DataError.Network {
    return when(statusCode) {
        HttpStatusCode.Conflict -> DataError.Network.CONFLICT
        HttpStatusCode.BadRequest -> DataError.Network.BAD_REQUEST
        HttpStatusCode.Forbidden -> DataError.Network.FORBIDDEN
        HttpStatusCode.MethodNotAllowed -> DataError.Network.METHOD_NOT_ALLOWED
        HttpStatusCode.Unauthorized -> DataError.Network.UNAUTHORIZED
        HttpStatusCode.InternalServerError -> DataError.Network.SERVER_ERROR
        else -> DataError.Network.UNKNOWN
    }
}