/*
 * Copyright 2010-2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.csvparser

import kotlinx.cinterop.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import platform.posix.*
import runtime.client.LambdaRuntimeClient

fun parseLine(line: String, separator: Char) : List<String> {
    val result = mutableListOf<String>()
    val builder = StringBuilder()
    var quotes = 0
    for (ch in line) {
        when {
            ch == '\"' -> {
                quotes++
                builder.append(ch)
            }
            (ch == '\n') || (ch ==  '\r') -> {}
            (ch == separator) && (quotes % 2 == 0) -> {
                result.add(builder.toString())
                builder.setLength(0)
            }
            else -> builder.append(ch)
        }
    }
    return result
}

@ImplicitReflectionSerializer
fun main() {
    LambdaRuntimeClient().run {
        val apigwReq: ApiGatewayRequest = Json.nonstrict.parse(ApiGatewayRequest.serializer(), it.payload)
        val request: CsvParseRequest = Json.parse(CsvParseRequest.serializer(), apigwReq.body)
        println("Parsed request: $request. Opening file...")
        val file = fopen(request.fileName, "r") ?: throw Exception("cannot open input file")

        val keyValue = mutableMapOf<String, Int>()

        try {
            memScoped {
                val bufferLength = 64 * 1024
                val buffer = allocArray<ByteVar>(bufferLength)

                for (i in 1..request.count) {
                    val nextLine = fgets(buffer, bufferLength, file)?.toKString()
                    if (nextLine == null || nextLine.isEmpty()) break

                    val records = parseLine(nextLine, ',')
                    val key = records[request.column]
                    val current = keyValue[key] ?: 0
                    keyValue[key] = current + 1
                }
            }
        } finally {
            fclose(file)
        }
        val response = Json.stringify(CsvParseResponse.serializer(), CsvParseResponse(keyValue))
        val apigwResp = ApiGatewayResponse(
                statusCode = 200,
                headers = null,
                body = response
        )
        val serializedResp = Json.stringify(ApiGatewayResponse.serializer(), apigwResp)
        println("Sending response to runtime: $apigwResp")
        serializedResp
    }
}
