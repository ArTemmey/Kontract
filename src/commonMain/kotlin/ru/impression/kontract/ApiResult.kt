package ru.impression.kontract

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


interface ApiError

@Serializable
object UnknownError : ApiError

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

@Serializable
@PublishedApi
internal class SerializableResult(
    val type: String,
    val value: JsonElement?,
    val error: JsonElement?
)