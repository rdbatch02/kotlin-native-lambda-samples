package sample.csvparser

import kotlinx.serialization.Serializable

@Serializable
data class ApiGatewayRequest(
        val body: String
)