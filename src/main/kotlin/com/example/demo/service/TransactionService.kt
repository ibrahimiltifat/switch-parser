package com.example.demo.service

import com.example.demo.entity.Transaction
import Model
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.springframework.stereotype.Service

@Service
class TransactionService {


    fun parser(string: String){

//        val transaction = Transaction(1,"key","value")
        printJsonKeyValues(string)
    }

    fun printJsonKeyValues(jsonString: String) {
        val transaction = Json.decodeFromString<Model>(jsonString)
        printKeyValues("", transaction)
    }

    fun printKeyValues(prefix: String, obj: Any?) {
        when (obj) {
            is Map<*, *> -> {
                obj.forEach { (key, value) ->
                    printKeyValues("$prefix$key.", value)
                }
            }
            is List<*> -> {
                obj.forEachIndexed { index, item ->
                    printKeyValues("$prefix$index.", item)
                }
            }
            else -> {
                println("$prefix$obj")
            }
        }
    }
}