package ru.impression.kontract

import io.ktor.client.features.*
import io.ktor.http.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

interface ApiError

class ServerResponseError(val cause: ServerResponseException) : ApiError

inline fun <reified T : Enum<*>> polymorphicEnumSerializer(noinline factory: (String) -> T): KSerializer<T> =
    PolymorphicEnumSerializer(T::class, factory)

@PublishedApi
internal class PolymorphicEnumSerializer<T : Enum<*>>(clazz: KClass<T>, private val factory: (String) -> T) :
    KSerializer<T> {

    override val descriptor = buildClassSerialDescriptor(clazz.simpleName!!) {
        element("name", buildClassSerialDescriptor("name"))
    }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, value.name)
        }
    }

    override fun deserialize(decoder: Decoder): T {
        var name: String? = null
        decoder.decodeStructure(descriptor) {
            name = decodeStringElement(descriptor, 0)
        }
        return factory(name!!)
    }
}

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
    override val error: ApiError,
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
    @Contextual
    val error: ApiError?
)