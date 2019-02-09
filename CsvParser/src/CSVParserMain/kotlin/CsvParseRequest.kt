package sample.csvparser

import kotlinx.serialization.Serializable

@Serializable
data class CsvParseRequest(
        val fileName: String,
        val column: Int,
        val count: Int
)