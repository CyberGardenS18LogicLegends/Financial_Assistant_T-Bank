package com.t_bank.financial_assistant.features.home

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import android.view.View
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.t_bank.common.BaseFragment
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.adapters.HorizontalSpaceItemDecoration
import com.t_bank.financial_assistant.adapters.TransactionAdapter
import com.t_bank.financial_assistant.custom_view.MultiColorProgressBarDrawable
import com.t_bank.financial_assistant.databinding.AddTransactionBottomSheetLayoutBinding
import com.t_bank.financial_assistant.databinding.FragmentHomeBinding
import com.t_bank.financial_assistant.features.add_transaction.preview.AddTransactionViewModel
import com.t_bank.local.room.entity.TransactionEntity
import com.t_bank.local.room.entity.TransactionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    R.layout.fragment_home,
    FragmentHomeBinding::inflate
) {

    private val viewModel: AddTransactionViewModel by activityViewModels()

    private lateinit var expensesAdapter: TransactionAdapter
    private lateinit var incomeAdapter: TransactionAdapter

    override fun setupUiOnViewCreated() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_menu)
        bottomNavigationView.visibility = View.VISIBLE

        // Инициализация адаптеров
        expensesAdapter = TransactionAdapter(
            onDeleteClick = { transaction ->
                val transactionEntity = transactionToTransactionEntity(transaction)
                viewModel.deleteTransaction(transactionEntity)
            }
        )
        incomeAdapter = TransactionAdapter(
            onDeleteClick = { transaction ->
                val transactionEntity = transactionToTransactionEntity(transaction)
                viewModel.deleteTransaction(transactionEntity)
            }
        )

        // Установка адаптеров для RecyclerView
        binding.expensesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.expensesRecyclerView.adapter = expensesAdapter
        binding.expensesRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(16))

        binding.incomeRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.incomeRecyclerView.adapter = incomeAdapter
        binding.incomeRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(16))

        // Загрузка данных из базы данных
        loadTransactions()

        // Пример категорий
        val categories = listOf(
            MultiColorProgressBarDrawable.Category(color = 0xFF0000, percentage = 0.3f), // 30% красный
            MultiColorProgressBarDrawable.Category(color = 0x00FF00, percentage = 0.5f), // 50% зеленый
            MultiColorProgressBarDrawable.Category(color = 0x0000FF, percentage = 0.2f)  // 20% синий
        )

        // Создайте пользовательский Drawable
        val customDrawable = MultiColorProgressBarDrawable(categories)

        binding.progressBarIncome.progressDrawable = customDrawable
        binding.progressBarIncome.invalidate()

        binding.progressBarExpenses.progressDrawable = customDrawable
        binding.progressBarExpenses.invalidate()

        // Обработчики нажатий на кнопки
        binding.addExpensesBtn.setOnClickListener {
            viewModel.transactionData.value?.let { transactionData ->
                transactionData.transactionType = TransactionType.EXPENSE
                findNavController().navigate(R.id.action_homeFragment_to_addPageFragment)
            }
        }

        binding.addIncomeBtn.setOnClickListener {
            viewModel.transactionData.value?.let { transactionData ->
                transactionData.transactionType = TransactionType.INCOME
                findNavController().navigate(R.id.action_homeFragment_to_addPageFragment)
            }
        }

        // Наблюдатель за результатом удаления транзакции
        lifecycleScope.launch {
            viewModel.transactionResult.collect { result ->
                if (result.isSuccess) {
                    loadTransactions()
                }
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

    private fun loadTransactions() {
        lifecycleScope.launch {
            viewModel.expenses.collect { expenses ->
                val totalExpenses = expenses.sumOf { it.amount }
                binding.amountExpensesText.text = totalExpenses.toString()
                expensesAdapter.submitList(expenses.map { transactionEntityToTransaction(it) })
            }
        }

        lifecycleScope.launch {
            viewModel.income.collect { income ->
                val totalIncome = income.sumOf { it.amount }
                binding.amountIncomeText.text = totalIncome.toString()
                incomeAdapter.submitList(income.map { transactionEntityToTransaction(it) })
            }
        }
    }

    private fun transactionEntityToTransaction(transactionEntity: TransactionEntity): TransactionAdapter.Transaction {
        return TransactionAdapter.Transaction(
            date = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault()).format(transactionEntity.dateCreated),
            icon = R.color.textColorHint,
            source = transactionEntity.source,
            category = transactionEntity.category,
            amount = transactionEntity.amount.toString(),
            comment = transactionEntity.comment
        )
    }
}
