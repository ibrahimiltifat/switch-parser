package com.example.demo.service

import com.example.demo.entity.Detail
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


    fun parser(json: String): List<Map<String,Any?>> {
        val list = transactionRepository.findAll()
        val deserializedData = dynamicDeserialize(json, list)
        val response = mutableListOf<Map<String,Any?>>()
        deserializedData.forEach { (key, value) ->
            println("$key: $value")
            val map = mutableMapOf<String,Any?>(key to value)
            response.add(map)
        }
        return response
    }



    fun dynamicDeserialize(json: String, transactions: List<Transaction>): Map<String, Any?> {
        val jsonElement = Json.parseToJsonElement(json).jsonObject
        val result = mutableMapOf<String, Any?>()

        transactions.forEach { transaction ->
            val key = transaction.key
            val type = transaction.type

            if (type == "object") {
                if(jsonElement[key] != null){
                    println("key $key")
                    val map = getObjValue(jsonElement[key].toString(), transaction.id)
                    for(m in map){
                        result[key+"."+m.key]= m.value
                    }
                }
            } else {
                val jsonValue = jsonElement[key]?.jsonPrimitive
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
        }
        return result
    }

    fun getObjValue(json: String, id: Long): Map<String, Any?> {
        val detailList = detailRepository.findAllByTransactionKey(transactionRepository.findById(id).get())
        var transactions:MutableList<Transaction> = mutableListOf()
        for (detail in detailList){
            transactions.add(transactionRepository.findById(detail.transactionVal.id).get())
        }
        println("calling parser")
        val result= dynamicDeserialize(json,transactions)
        return result

    }

}