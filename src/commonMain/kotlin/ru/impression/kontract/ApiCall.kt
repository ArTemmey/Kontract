package ru.impression.kontract

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.reflect.KClass

val KClass<out ApiCall.Type>.httpMethod
    get() = when (this) {
        ApiCall.Type.Get::class -> HttpMethod.Get
        ApiCall.Type.Post::class -> HttpMethod.Post
        ApiCall.Type.Put::class -> HttpMethod.Put
        ApiCall.Type.Patch::class -> HttpMethod.Patch
        ApiCall.Type.Delete::class -> HttpMethod.Delete
        else -> throw Exception()
    }

class ApiCall<T : ApiCall.Type, D>(
    val contract: ApiContract,
    val requestBody: Any?
) {

    sealed class Type {
        object Get : Type()
        object Post : Type()
        object Put : Type()
        object Patch : Type()
        object Delete : Type()
    }
}

suspend inline fun <reified T : ApiCall.Type, reified D> ApiCall<T, D>.execute(
    client: HttpClient,
    builder: (HttpRequestBuilder.() -> Unit) = {}
): ApiResult<D> {
    try {
        val result: SerializableResult = try {
            client.request {
                contentType(ContentType.Application.Json)
                method = T::class.httpMethod
                requestBody?.let { body = it }
                builder()
                url(contract.baseUrl + contract.parametrizedPath)
                contract.headers.forEach {
                    header(it.key, it.value)
                }
                contract.queryParams.forEach {
                    parameter(it.key, it.value)
                }
            }
        } catch (e: ClientRequestException) {
            e.response.receive()
        }
        return when (result.type) {
            "ok" -> Ok(
                result.value?.let { contract.json.decodeFromString<D>(it) } as D
            )
            "err" -> Err(result.error!!)
            else -> throw Exception("Invalid result type")
        }
    } catch (e: Exception) {
        return Err(ApiError.FromException(e))
    }
}