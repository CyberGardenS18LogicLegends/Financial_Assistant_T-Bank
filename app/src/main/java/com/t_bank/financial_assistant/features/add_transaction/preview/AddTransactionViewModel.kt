package com.t_bank.financial_assistant.features.add_transaction.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t_bank.local.room.dao.TransactionDao
import com.t_bank.local.room.entity.TransactionEntity
import com.t_bank.local.room.entity.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

    private val _transactionData = MutableStateFlow<TransactionData?>(null)
    val transactionData: StateFlow<TransactionData?> get() = _transactionData

    private val _transactionResult = MutableSharedFlow<Result<Unit>>()
    val transactionResult: SharedFlow<Result<Unit>> get() = _transactionResult

    private val _expenses = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val expenses: StateFlow<List<TransactionEntity>> get() = _expenses

    private val _income = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val income: StateFlow<List<TransactionEntity>> get() = _income

    fun saveTransactionData(dateFrom: String, dateTo: String, transactionType: TransactionType) {
        _transactionData.value = TransactionData(dateFrom, dateTo, transactionType)
    }

    init {
        getAllTransactions()
    }

    fun submitTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            try {
                transactionDao.insertTransaction(transaction)
                _transactionResult.emit(Result.success(Unit))
            } catch (e: Exception) {
                _transactionResult.emit(Result.failure(e))
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            try {
                transactionDao.deleteTransaction(transaction)
                _transactionResult.emit(Result.success(Unit))
            } catch (e: Exception) {
                _transactionResult.emit(Result.failure(e))
            }
        }
    }

    fun getAllTransactions()  { // Flow<List<TransactionEntity>>
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        val startDate = calendar.time

        getTransactionsBetweenDates(startDate, currentDate)
    }

    fun getTransactionsBetweenDates(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            transactionDao.getTransactionsBetweenDates(startDate, endDate).collect { transactions ->
                _expenses.value = transactions.filter { it.type == TransactionType.EXPENSE }.reversed()
                _income.value = transactions.filter { it.type == TransactionType.INCOME }.reversed()
            }
        }
    }
}


data class TransactionData(
    val dateFrom: String,
    val dateTo: String,
    var transactionType: TransactionType
)

