package com.example.demo.repository

import com.example.demo.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository :JpaRepository<Transaction,Long>