package ru.impression.kontract

import io.ktor.client.features.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


interface ApiError

@Serializable
object UnknownError : ApiError

class ServerResponseError(val cause: ServerResponseException) : ApiError

sealed class ApiResult<out V> {
    abstract val value: V?
    abstract val error: ApiError?
}

data class Ok<out V>(override val value: V) : ApiResult<V>() {
    override val error = null
}

data class Err(override val error: ApiError = UnknownError) : ApiResult<Nothing>() {
    override val value = null
}

inline fun <V> ApiResult<V>.onSuccess(action: (V) -> Unit) = this.also { if (this is Ok<V>) action(value) }

inline fun <V> ApiResult<V>.onFailure(action: (ApiError) -> Unit) = this.also { if (this is Err) action(error) }

inline fun <V, U> ApiResult<V>.map(transform: (V) -> U) = when (this) {
    is Ok<V> -> Ok(transform(value))
    is Err -> this
}

@Serializable
@PublishedApi
internal class SerializableResult(
    val type: String,
    val value: JsonElement?,
    val error: JsonElement?
)