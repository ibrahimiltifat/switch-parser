package com.example.demo.service

import com.example.demo.entity.Transaction
import com.example.demo.repository.DetailRepository
import com.example.demo.repository.FieldsetRepository
import com.example.demo.repository.TransactionRepository
import kotlinx.serialization.json.*
import org.springframework.stereotype.Service

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val detailRepository: DetailRepository,
    private val fieldsetRepository: FieldsetRepository
) {

    fun parser(json: String, channelId: Long): List<Map<String, Any?>> {
        val transactions = getTransactionsForChannel(channelId) // Get transactions based on channelId
        val deserializedData = dynamicDeserialize(json, transactions)
        val response = mutableListOf<Map<String, Any?>>()
        deserializedData.forEach { (key, value) ->
            println("$key: $value")
            val map = mutableMapOf<String, Any?>(key to value)
            response.add(map)
        }
        return response
    }

    fun getTransactionsForChannel(channelId: Long): List<Transaction> {
        val fieldSets = fieldsetRepository.findByChannelId(channelId)
        val transactionIds = fieldSets.map { it.transactionId }
        return transactionRepository.findAllById(transactionIds)
    }

    fun dynamicDeserialize(json: String, transactions: List<Transaction>): Map<String, Any?> {
        val jsonElement = Json.parseToJsonElement(json).jsonObject
        val result = mutableMapOf<String, Any?>()

        transactions.forEach { transaction ->
            val key = transaction.key
            val type = transaction.type

            if (type == "object") {
                if (jsonElement[key] != null) {
                    val map = getObjValue(jsonElement[key].toString(), transaction.id)
                    for (m in map) {
                        result[key + "." + m.key] = m.value
                    }
                } else {
                    var list = getKeys(transaction)
                    for (l in list) {
                        result[l] = null
                    }
                }
            } else if (type == "array") {
                val list = jsonElement[key]?.jsonArray
                if (list != null) {
                    val arrayDetail = detailRepository.findByTransactionKey(transaction).transactionVal
                    val arrayDetailMapping = detailRepository.findAllByTransactionKey(arrayDetail)
                    for ((index, l) in list.withIndex()) {
                        if (arrayDetailMapping.isEmpty()) {
                            val transactionType = transactionRepository.findById(arrayDetail.id).get().type
                            result[key + index] = getJsonPrimitiveValue(transactionType, l.jsonPrimitive)
                        } else {
                            val map = getObjValue(l.toString(), arrayDetail.id)
                            for (m in map) {
                                result[key + index + "." + m.key] = m.value
                            }
                        }
                    }
                } else {
                    var list = getKeys(transaction)
                    for (l in list) {
                        result[l] = null
                    }
                }
            } else {
                val jsonValue = jsonElement[key]?.jsonPrimitive
                if (jsonValue != null) {
                    result[key] = getJsonPrimitiveValue(type, jsonValue)
                } else {
                    result[key] = null
                }
            }
        }
        return result
    }

    fun getJsonPrimitiveValue(type: String, jsonValue: JsonPrimitive) =
        when (type) {
            "String" -> jsonValue.content
            "Long" -> jsonValue.longOrNull
            "Int" -> jsonValue.intOrNull
            "Boolean" -> jsonValue.booleanOrNull
            else -> null
        }


    fun getObjValue(json: String, id: Long): Map<String, Any?> {
        val detailList = detailRepository.findAllByTransactionKey(transactionRepository.findById(id).get())
        var transactions: MutableList<Transaction> = mutableListOf()
        val jsonElement = Json.parseToJsonElement(json)
        if (detailList.isEmpty()) {
            transactions.add(transactionRepository.findById(id).get())


        } else {
            for (detail in detailList) {
                transactions.add(transactionRepository.findById(detail.transactionVal.id).get())
            }
        }
        println("calling parser")
        val result = dynamicDeserialize(jsonElement.toString(), transactions)
        return result
    }

    fun getKeys(transaction: Transaction): List<String> {
        var respose = ""
        var list = mutableListOf<String>()
        var details = detailRepository.findAllByTransactionKey(transaction)
        var transactions = details.map { it.transactionVal }

        for (trx in transactions) {
            if (trx.type != "object" && trx.type != "array") {
                if (transaction.type == "object")
                    respose = "${transaction.key}.${trx.key}"
                else
                    respose = "${transaction.key}0.${trx.key}"
                list.add(respose)
            } else {
                val internalList = getKeys(trx)
                for (il in internalList) {
                    if (transaction.type == "object")
                        list.add("${transaction.key}.$il")
                    else {
                        list.add("${transaction.key}0.$il")
                    }
                }
            }

        }
        return list
    }

}