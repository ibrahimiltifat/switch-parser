package com.example.demo.controllers

import com.example.demo.model.JsonRequestDTO
import com.example.demo.service.TransactionService
import org.springframework.web.bind.annotation.*
import org.springframework.http.*

@RestController
@RequestMapping("/api")
class UserController(
    private val transactionService: TransactionService
) {

    // GET /api/users
    @GetMapping("/users")
    fun ResponseEntity(@RequestBody request: JsonRequestDTO): ResponseEntity<List<Map<String,Any?>>> {
        val response = transactionService.parser(request.input)
        return ResponseEntity.ok(response)
    }

}
