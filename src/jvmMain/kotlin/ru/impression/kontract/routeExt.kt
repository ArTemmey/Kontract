package ru.impression.kontract

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.full.createInstance

inline fun <reified C : ApiContract, reified T : ApiCall.Type, reified D> Route.method(
    function: KFunction1<C, ApiCall<T, D>>,
    noinline body: suspend ApiMethodContext<C>.() -> ApiResult<D>
) {
    method<C, T, Unit, D>(body)
}


inline fun <reified C : ApiContract, reified T : ApiCall.Type, reified R : Any, reified D> Route.method(
    function: KFunction2<C, R, ApiCall<T, D>>,
    noinline body: suspend ApiMethodContext<C>.(R) -> ApiResult<D>
) {
    method<C, T, R, D>(body1Arg = body)
}

@PublishedApi
internal inline fun <reified C : ApiContract, reified T : ApiCall.Type, reified R : Any, reified D> Route.method(
    noinline bodyNoArgs: (suspend ApiMethodContext<C>.() -> ApiResult<D>)? = null,
    noinline body1Arg: (suspend ApiMethodContext<C>.(R) -> ApiResult<D>)? = null
) {
    route(C::class.createInstance().path) {
        method(T::class.httpMethod) {
            handle {
                val contract = C::class.createInstance()
                contract.callParser = CallParser(this, contract.json)
                val context = ApiMethodContext(contract, this)
                val result = bodyNoArgs?.invoke(context) ?: body1Arg?.invoke(context, call.receive()) ?: return@handle
                val serializableResult = SerializableResult(
                    when (result) {
                        is Err -> "err"
                        is Ok -> "ok"
                    },
                    result.value?.let { contract.json.encodeToString(it) },
                    result.error
                )
                call.respond(result.status, serializableResult)
            }
        }
    }
}