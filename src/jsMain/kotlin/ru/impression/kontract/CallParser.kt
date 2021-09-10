package ru.impression.kontract

@PublishedApi
internal actual class CallParser {

    @PublishedApi
    internal actual inline fun <reified T> getPathParam(name: String): T? = throw Exception()

    @PublishedApi
    internal actual inline fun <reified T> getQueryParam(name: String): T? = throw Exception()

    @PublishedApi
    internal actual inline fun <reified T> getHeader(name: String): T? = throw Exception()
}