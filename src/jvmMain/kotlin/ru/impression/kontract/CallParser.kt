package ru.impression.kontract

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@PublishedApi
internal actual class CallParser(val pipelineContext: PipelineContext<*, ApplicationCall>, val json: Json) {

    @PublishedApi
    internal actual fun getPathParam(name: String): String? = pipelineContext.call.parameters[name]

    @PublishedApi
    internal actual inline fun <reified T> getQueryParam(name: String): T? {
        return json.decodeFromString(pipelineContext.call.request.queryParameters[name] ?: return null)
    }

    @PublishedApi
    internal actual fun getHeader(name: String): String? = pipelineContext.call.request.header(name)
}