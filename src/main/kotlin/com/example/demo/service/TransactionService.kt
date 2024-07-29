package com.example.demo.service

import com.example.demo.entity.Transaction
import com.example.demo.repository.DetailRepository
import com.example.demo.repository.TransactionRepository
import kotlinx.serialization.json.*
import org.springframework.stereotype.Service

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val detailRepository: DetailRepository
) {

    fun parser(json: String): List<Map<String, Any?>> {
        val list = transactionRepository.findAll()
        val deserializedData = dynamicDeserialize(Json.parseToJsonElement(json).jsonObject, list)
        val response = mutableListOf<Map<String, Any?>>()
        deserializedData.forEach { (key, value) ->
            println("$key: $value")
            val map = mutableMapOf<String, Any?>(key to value)
            response.add(map)
        }
        return response
    }

    fun dynamicDeserialize(jsonObject: JsonObject, transactions: List<Transaction>): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()

        transactions.forEach { transaction ->
            val key = transaction.key
            val type = transaction.type

            when (type) {
                "object" -> {
                    if (jsonObject[key] != null) {
                        val map = getObjOrArrayValue(jsonObject[key]!!.toString(), transaction.id)
                        map.forEach { (mapKey, mapValue) ->
                            result["$key.$mapKey"] = mapValue
                        }
                    }
                }
                "array" -> {
                    if (jsonObject[key] != null) {
                        val array = getObjOrArrayValue(jsonObject[key]!!.toString(), transaction.id)
                        array.forEach { (arrayKey, arrayValue) ->
                            result["$key$arrayKey"] = arrayValue
                        }
                    }
                }
                else -> {
                    val jsonValue = jsonObject[key]?.jsonPrimitive
                    if (jsonValue != null) {
                        result[key] = when (type) {
                            "String" -> jsonValue.content
                            "Long" -> jsonValue.longOrNull
                            "Int" -> jsonValue.intOrNull
                            "Boolean" -> jsonValue.booleanOrNull
                            else -> null
                        }
                    }
                }
            }
        }
        return result
    }

    fun getObjOrArrayValue(json: String, id: Long): Map<String, Any?> {
        val detailList = detailRepository.findAllByTransactionKey(transactionRepository.findById(id).get())
        val transactions = detailList.map { transactionRepository.findById(it.transactionVal.id).get() }
        val jsonElement = Json.parseToJsonElement(json)

        return if (jsonElement is JsonArray) {
            parseArray(jsonElement, transactions)
        } else {
            dynamicDeserialize(jsonElement.jsonObject, transactions)
        }
    }

    fun parseArray(jsonArray: JsonArray, transactions: List<Transaction>): Map<String, Any?> {
        val arrayResult = mutableMapOf<String, Any?>()
        jsonArray.forEachIndexed { index, element ->
            when (element) {
                is JsonObject -> {
                    val map = dynamicDeserialize(element, transactions)
                    map.forEach { (mapKey, mapValue) ->
                        arrayResult["$index.$mapKey"] = mapValue
                    }
                }
                is JsonArray -> {
                    val nestedArray = parseArray(element, transactions)
                    nestedArray.forEach { (nestedKey, nestedValue) ->
                        arrayResult["$index.$nestedKey"] = nestedValue
                    }
                }
                else -> {
                    arrayResult["$index"] = element.jsonPrimitive.contentOrNull
                }
            }
        }

        return arrayResult
    }
}
