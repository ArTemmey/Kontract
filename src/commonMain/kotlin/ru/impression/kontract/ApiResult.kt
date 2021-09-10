package ru.impression.kontract

import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

interface ApiError

@Serializable
object UnknownError : ApiError

class ServerResponseError(val cause: ServerResponseException) : ApiError

sealed class ApiResult<out V> {
    abstract val value: V?
    abstract val error: ApiError?

    @PublishedApi
    internal abstract val status: HttpStatusCode
}

data class Ok<out V>(override val value: V, override val status: HttpStatusCode = HttpStatusCode.OK) : ApiResult<V>() {
    override val error = null
}

data class Err(
    override val error: ApiError = UnknownError,
    override val status: HttpStatusCode = HttpStatusCode.UnprocessableEntity
) : ApiResult<Nothing>() {
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