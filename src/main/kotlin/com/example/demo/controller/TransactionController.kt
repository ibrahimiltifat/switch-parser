package com.example.demo.controllers

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
    fun transactionController(@RequestBody input: String): ResponseEntity<String> {
        println(input)
        transactionService.parser(input)
        return ResponseEntity.ok("users")
    }

}
