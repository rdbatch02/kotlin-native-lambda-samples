package sample.csvparser

import kotlinx.serialization.Serializable

@Serializable
data class CsvParseResponse(val response: Map<String, Int>)