package com.t_bank.financial_assistant.features.add_transaction.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t_bank.local.room.dao.TransactionDao
import com.t_bank.local.room.entity.TransactionEntity
import com.t_bank.local.room.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _transactionResult = MutableSharedFlow<Result<Unit>>()
    val transactionResult: SharedFlow<Result<Unit>> get() = _transactionResult

    fun submitTransaction(dateCreated: Date, type: TransactionType, amount: Double, comment: String, category: String, source: String) {
        viewModelScope.launch {
            try {
                val transaction = TransactionEntity(
                    dateCreated = dateCreated,
                    type = type,
                    amount = amount,
                    comment = comment,
                    category = category,
                    source = source
                )
                transactionDao.insertTransaction(transaction)
                _transactionResult.emit(Result.success(Unit))
            } catch (e: Exception) {
                _transactionResult.emit(Result.failure(e))
            }
        }
    }
}


