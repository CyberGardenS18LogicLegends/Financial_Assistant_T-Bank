package com.t_bank.financial_assistant.features.add_transaction.adding_screen

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.t_bank.common.BaseFragment
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.adapters.HorizontalSpaceItemDecoration
import com.t_bank.financial_assistant.adapters.TransactionAdapter
import com.t_bank.financial_assistant.databinding.AddTransactionBottomSheetLayoutBinding
import com.t_bank.financial_assistant.databinding.FragmentAddBlankBinding
import com.t_bank.financial_assistant.features.add_transaction.preview.AddTransactionViewModel
import com.t_bank.local.room.entity.TransactionEntity
import com.t_bank.local.room.entity.TransactionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddBlankFragment : BaseFragment<FragmentAddBlankBinding>(
    R.layout.fragment_add_blank,
    FragmentAddBlankBinding::inflate
) {

    private val viewModel: AddTransactionViewModel by activityViewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    @SuppressLint("SetTextI18n")
    override fun setupUiOnViewCreated() {
        binding.typeTransactionText.text = if(viewModel.transactionData.value?.transactionType == TransactionType.INCOME) "Доходы" else "Расходы"

        // Инициализация RecyclerView
        transactionAdapter = TransactionAdapter(
            onDeleteClick = { transaction ->
                val transactionEntity = transactionToTransactionEntity(transaction)
                viewModel.deleteTransaction(transactionEntity)
            }
        )
        binding.transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transactionRecyclerView.adapter = transactionAdapter
        binding.transactionRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(16))

        // Заполнение RecyclerView данными из базы данных
        viewModel.transactionData.value?.let { transactionData ->
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val startDate = dateFormat.parse(transactionData.dateFrom)
            val endDate = dateFormat.parse(transactionData.dateTo)

            viewModel.getTransactionsBetweenDates(startDate, endDate)

            lifecycleScope.launch {
                viewModel.expenses.collect { expenses ->
                    val filteredTransactions = expenses
                        .filter { it.type == transactionData.transactionType }
                        .map { transactionEntity ->
                            TransactionAdapter.Transaction(
                                date = dateFormat.format(transactionEntity.dateCreated),
                                source = transactionEntity.source,
                                category = transactionEntity.category,
                                amount = transactionEntity.amount.toString(),
                                comment = transactionEntity.comment
                            )
                        }
                    transactionAdapter.submitList(filteredTransactions)
                }
            }

            lifecycleScope.launch {
                viewModel.income.collect { income ->
                    val filteredTransactions = income
                        .filter { it.type == transactionData.transactionType }
                        .map { transactionEntity ->
                            TransactionAdapter.Transaction(
                                date = dateFormat.format(transactionEntity.dateCreated),
                                source = transactionEntity.source,
                                category = transactionEntity.category,
                                amount = transactionEntity.amount.toString(),
                                comment = transactionEntity.comment
                            )
                        }
                    transactionAdapter.submitList(filteredTransactions)
                }
            }

            binding.dateText.text = "${transactionData.dateFrom}-${transactionData.dateTo}"
        }

        binding.submitButton.setOnClickListener {
            findNavController().navigate(R.id.action_addBlankFragment_to_addPageFragment)
        }

        lifecycleScope.launch {
            viewModel.transactionResult.collect { result ->
                handleTransactionResult(result)
            }
        }
    }

    private fun transactionToTransactionEntity(transaction: TransactionAdapter.Transaction): TransactionEntity {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
        val dateCreated = dateFormat.parse(transaction.date) ?: Date()
        val amount = transaction.amount.toDoubleOrNull() ?: 0.0

        return TransactionEntity(
            dateCreated = dateCreated,
            type = if (transaction.amount.startsWith("-")) TransactionType.EXPENSE else TransactionType.INCOME,
            amount = amount,
            comment = transaction.comment,
            category = transaction.category,
            source = transaction.source
        )
    }

    private fun handleTransactionResult(result: Result<Unit>) {
        when {
            result.isSuccess -> {
                Toast.makeText(requireContext(), "Транзакция удалена", Toast.LENGTH_SHORT).show()
            }
            result.isFailure -> {
                Toast.makeText(requireContext(), "Ошибка: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}