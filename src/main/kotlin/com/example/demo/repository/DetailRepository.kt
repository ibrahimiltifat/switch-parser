package com.example.demo.repository

import com.example.demo.entity.Detail
import com.example.demo.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DetailRepository :JpaRepository<Detail,Long>{
    fun findAllByTransactionKey(transactionKey: Transaction): List<Detail>
    fun findByTransactionKey(transactionKey: Transaction): Detail
}