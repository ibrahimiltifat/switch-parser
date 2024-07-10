package com.example.demo.service

import com.example.demo.entity.Transaction
import com.example.demo.repository.TransactionRepository
import kotlinx.serialization.json.*
import org.springframework.stereotype.Service

@Service
class TransactionService(private val transactionRepository: TransactionRepository) {


    fun parser(string: String) {
        printJsonKeyValues(string)
    }

    fun printJsonKeyValues(json: String) {
        val list =  transactionRepository.findAll()
        val deserializedData = dynamicDeserialize(json, list)
        deserializedData.forEach { (key, value) ->
            println("$key: $value")
        }
    }

    fun dynamicDeserialize(json: String, transactions: List<Transaction>): Map<String, Any?> {
        val jsonElement = Json.parseToJsonElement(json).jsonObject
        val result = mutableMapOf<String, Any?>()

        transactions.forEach { transaction ->
            val key = transaction.key
            val type = transaction.type

            val jsonValue = jsonElement[key]?.jsonPrimitive
            println(jsonValue)
            if (jsonValue != null) {
                result[key] = when (type) {
                    "String" -> jsonValue.content
                    "Long" -> jsonValue.longOrNull
                    "Int" -> jsonValue.intOrNull
                    "Boolean" -> jsonValue.booleanOrNull
                    // Add other types as needed
                    else -> null
                }
            }
        }
        return result
    }


}