package ru.impression.kontract

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@PublishedApi
internal actual class CallParser(@PublishedApi internal val pipelineContext: PipelineContext<*, ApplicationCall>) {

    @PublishedApi
    internal actual inline fun <reified T> getPathParam(name: String): T? {
        return Json.decodeFromString(pipelineContext.call.parameters[name] ?: return null)
    }

    @PublishedApi
    internal actual inline fun <reified T> getQueryParam(name: String): T? {
        return Json.decodeFromString(pipelineContext.call.request.queryParameters[name] ?: return null)
    }

    @PublishedApi
    internal actual inline fun <reified T> getHeader(name: String): T? {
        return Json.decodeFromString(pipelineContext.call.request.header(name) ?: return null)
    }
}