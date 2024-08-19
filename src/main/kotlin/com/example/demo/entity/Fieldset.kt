package com.example.demo.entity

import jakarta.persistence.*
import lombok.Data

@Entity
@Table(name = "fieldset")
@Data
data class Fieldset(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val channelId: Long,
    val transactionId: Long
)
