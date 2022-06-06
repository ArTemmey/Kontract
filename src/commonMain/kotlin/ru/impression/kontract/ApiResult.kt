package ru.impression.kontract

import io.ktor.http.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

interface ApiError {
    class FromException(val cause: Exception) : ApiError
}

sealed class ApiResult<out V> {
    abstract val value: V?
    abstract val error: ApiError?
    abstract val status: HttpStatusCode
}

data class Ok<out V>(override val value: V, override val status: HttpStatusCode = HttpStatusCode.OK) : ApiResult<V>() {
    override val error = null
}

data class Err(
    override val error: ApiError,
    override val status: HttpStatusCode = HttpStatusCode(520, "Unknown Error")
) : ApiResult<Nothing>() {
    override val value = null
}

inline fun <V> ApiResult<V>.onSuccess(action: (V) -> Unit) = this.also { if (this is Ok<V>) action(value) }

inline fun <V> ApiResult<V>.onFailure(action: (ApiError) -> Unit) = this.also { if (this is Err) action(error) }

inline fun <V, U> ApiResult<V>.map(transform: (V) -> U) = when (this) {
    is Ok<V> -> Ok(transform(value))
    is Err -> this
}

inline fun <V, U> ApiResult<V>.flatMap(transform: (V) -> ApiResult<U>) = when (this) {
    is Ok<V> -> transform(value)
    is Err -> this
}

@Serializable
@PublishedApi
internal class SerializableResult(
    val type: String,
    val value: JsonElement?,
    @Contextual
    val error: ApiError?
)