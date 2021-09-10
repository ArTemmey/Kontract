package ru.impression.kontract

import kotlinx.serialization.json.Json
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class ApiContract {
    var baseUrl: String? = null
    var json: Json = Json
    abstract val path: String

    @PublishedApi
    internal val parametrizedPath: String
        get() {
            var result = path
            pathParams.forEach {
                result = result.replace("{${it.key}}", it.value.toString())
            }
            return result
        }

    @PublishedApi
    internal val headers = mutableMapOf<String, Any?>()

    @PublishedApi
    internal val pathParams = mutableMapOf<String, Any?>()

    @PublishedApi
    internal val queryParams = mutableMapOf<String, Any?>()

    @PublishedApi
    internal var callParser: CallParser? = null

    protected inline fun <reified T> header(name: String) = object : ReadWriteProperty<ApiContract, T?> {

        private var value: T? = null

        override fun getValue(thisRef: ApiContract, property: KProperty<*>) =
            callParser?.getHeader<T>(name) ?: value

        override fun setValue(thisRef: ApiContract, property: KProperty<*>, value: T?) {
            headers[name] = value
            this.value = value
        }
    }

    protected inline fun <reified T> pathParam(name: String) = object : ReadWriteProperty<ApiContract, T?> {

        private var value: T? = null

        override fun getValue(thisRef: ApiContract, property: KProperty<*>) =
            callParser?.getPathParam<T>(name) ?: value

        override fun setValue(thisRef: ApiContract, property: KProperty<*>, value: T?) {
            pathParams[name] = value
            this.value = value
        }
    }

    protected inline fun <reified T> queryParam(name: String) = object : ReadWriteProperty<ApiContract, T?> {

        private var value: T? = null

        override fun getValue(thisRef: ApiContract, property: KProperty<*>) =
            callParser?.getQueryParam<T>(name) ?: value

        override fun setValue(thisRef: ApiContract, property: KProperty<*>, value: T?) {
            queryParams[name] = value
            this.value = value
        }
    }

    protected inline fun <reified D> get(requestBody: Any? = null) =
        ApiCall<ApiCall.Type.Get, D>(this, requestBody)

    protected inline fun <reified D> post(requestBody: Any? = null) =
        ApiCall<ApiCall.Type.Post, D>(this, requestBody)

    protected inline fun <reified D> put(requestBody: Any? = null) =
        ApiCall<ApiCall.Type.Put, D>(this, requestBody)

    protected inline fun <reified D> patch(requestBody: Any? = null) =
        ApiCall<ApiCall.Type.Patch, D>(this, requestBody)

    protected inline fun <reified D> delete(requestBody: Any? = null) =
        ApiCall<ApiCall.Type.Delete, D>(this, requestBody)
}