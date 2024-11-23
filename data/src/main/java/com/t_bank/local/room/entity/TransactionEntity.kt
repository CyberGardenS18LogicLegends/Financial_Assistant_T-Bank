package com.t_bank.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateCreated: Date,
    val type: TransactionType,
    val amount: Double,
    val comment: String,
    val category: String,
    val source: String
)

enum class TransactionType {
    INCOME, EXPENSE
}
