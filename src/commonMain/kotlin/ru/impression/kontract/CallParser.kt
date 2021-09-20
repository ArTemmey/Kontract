package ru.impression.kontract

@PublishedApi
internal expect class CallParser {
    @PublishedApi
    internal fun getPathParam(name: String): String?

    @PublishedApi
    internal inline fun <reified T> getQueryParam(name: String): T?

    @PublishedApi
    internal fun getHeader(name: String): String?
}