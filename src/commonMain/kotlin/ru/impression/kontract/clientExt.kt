package ru.impression.kontract

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

suspend inline fun <reified T : ApiCall.Type, reified D> HttpClient.request(
    baseApiUrl: String,
    block: HttpRequestBuilder.() -> ApiCall<T, D>
): ApiResult<D> {
    val result = request<SerializableResult>(
        HttpRequestBuilder().apply {
            val apiCall = block()
            url(baseApiUrl + apiCall.contract.parametrizedPath)
            method = T::class.httpMethod
            apiCall.requestBody?.let { body = it }
            apiCall.queryParams.forEach {
                parameter(it.key, it.value)
            }
        }
    )
    return when (result.type) {
        "ok" -> Ok(result.value?.let { Json.decodeFromJsonElement<D>(it) } as D)
        "err" -> Err(result.error!!.let { Json.decodeFromJsonElement(it) })
        else -> throw Exception()
    }
}