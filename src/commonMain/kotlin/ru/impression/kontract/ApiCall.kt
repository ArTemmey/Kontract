package ru.impression.kontract

import io.ktor.http.*
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
    val path: String,
    val requestBody: Any?,
    val queryParams: Map<String, *>
) {

    sealed class Type {
        object Get : Type()
        object Post : Type()
        object Put : Type()
        object Patch : Type()
        object Delete : Type()
    }
}