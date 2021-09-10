package ru.impression.kontract

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.util.pipeline.*

class ApiMethodContext<C : ApiContract>(val contract: C, val pipelineContext: PipelineContext<*, ApplicationCall>) {

    val call get() = pipelineContext.call
}