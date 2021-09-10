package ru.impression.kontract

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
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
    val result: SerializableResult = try {
        client.request {
            url(contract.baseUrl + contract.parametrizedPath)
            method = T::class.httpMethod
            requestBody?.let { body = it }
            contract.headers.forEach {
                header(it.key, it.value)
            }
            contract.queryParams.forEach {
                parameter(it.key, it.value)
            }
            builder()
        }
    } catch (e: ClientRequestException) {
        e.response.receive()
    } catch (e: ServerResponseException) {
        return Err(ServerResponseError(e))
    }
    return when (result.type) {
        "ok" -> Ok(result.value?.let { Json.decodeFromJsonElement<D>(it) } as D)
        "err" -> Err(result.error!!.let { Json.decodeFromJsonElement(it) })
        else -> throw Exception()
    }
}