package sample.csvparser

import kotlinx.serialization.Serializable

@Serializable
data class ApiGatewayResponse(
        val statusCode: Int,
        val headers: Map<String, String>?,
        val body: String
)