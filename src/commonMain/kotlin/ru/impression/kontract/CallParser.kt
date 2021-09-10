package ru.impression.kontract

@PublishedApi
internal expect class CallParser {
    @PublishedApi
    internal inline fun <reified T> getPathParam(name: String): T?

    @PublishedApi
    internal inline fun <reified T> getQueryParam(name: String): T?

    @PublishedApi
    internal inline fun <reified T> getHeader(name: String): T?
}