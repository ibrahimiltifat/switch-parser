package com.example.demo.repository

import com.example.demo.entity.Fieldset
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FieldsetRepository :JpaRepository<Fieldset, Long>{
    fun findAllById(id: Long): List<Fieldset>
    fun findByChannelId(channelId: Long): List<Fieldset>

}