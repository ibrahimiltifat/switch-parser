package com.example.demo.entity

import jakarta.persistence.*
import lombok.Data

@Entity
@Table(name = "detail")
@Data
data class Detail(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "transaction_key")
    val transactionKey: Transaction,

    @ManyToOne
    @JoinColumn(name = "transaction_val")
    val transactionVal: Transaction
)
