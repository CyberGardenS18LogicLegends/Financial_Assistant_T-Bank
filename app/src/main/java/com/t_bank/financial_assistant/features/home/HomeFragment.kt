package com.t_bank.financial_assistant.features.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.t_bank.common.BaseFragment
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.adapters.HorizontalSpaceItemDecoration
import com.t_bank.financial_assistant.adapters.TransactionAdapter
import com.t_bank.financial_assistant.custom_view.MultiColorProgressBarDrawable
import com.t_bank.financial_assistant.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    R.layout.fragment_home,
    FragmentHomeBinding::inflate
) {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var expensesAdapter: TransactionAdapter
    private lateinit var incomeAdapter: TransactionAdapter

    override fun setupUiOnViewCreated() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_menu)
        bottomNavigationView.visibility = View.VISIBLE

        // Инициализация адаптеров
        expensesAdapter = TransactionAdapter()
        incomeAdapter = TransactionAdapter()

        // Установка адаптеров для RecyclerView
        binding.expensesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.expensesRecyclerView.adapter = expensesAdapter
        binding.expensesRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(16))

        binding.incomeRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.incomeRecyclerView.adapter = incomeAdapter
        binding.incomeRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(16))

        // Пример данных
        val transactions = listOf(
            TransactionAdapter.Transaction(
                date = "22 ноября 2024, 12:33",
                icon = R.color.textColorHint,
                name = "Алексей",
                category = "категория",
                amount = "-2000",
                comment = "комментарий \nк платежу \nк платежу"
            ),
            TransactionAdapter.Transaction(
                date = "22 ноября 2024, 12:33",
                icon = R.color.textColorHint,
                name = "Алексей",
                category = "категория",
                amount = "-2000",
                comment = "комментарий \nк платежу \nк платежу"
            ),
            // Добавьте другие транзакции по аналогии
        )

        // Установка данных в адаптеры
        expensesAdapter.submitList(transactions)
        incomeAdapter.submitList(transactions)

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

    }

}

