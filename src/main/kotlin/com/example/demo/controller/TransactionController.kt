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
    fun transactionController(@RequestBody request: JsonRequestDTO): ResponseEntity<String> {
        println(request.input)
        transactionService.parser(request.input)
        return ResponseEntity.ok("users")
    }

}
