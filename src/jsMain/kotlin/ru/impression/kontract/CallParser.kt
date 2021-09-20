package ru.impression.kontract

@PublishedApi
internal actual class CallParser {

    @PublishedApi
    internal actual fun getPathParam(name: String): String? = throw Exception()

    @PublishedApi
    internal actual inline fun <reified T> getQueryParam(name: String): T? = throw Exception()

    @PublishedApi
    internal actual fun getHeader(name: String): String? = throw Exception()
}