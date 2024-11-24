package com.t_bank.financial_assistant.features.add_transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.t_bank.common.BaseFragment
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.databinding.FragmentAddPageBinding
import com.t_bank.financial_assistant.features.add_transaction.preview.AddTransactionViewModel
import com.t_bank.local.room.entity.TransactionEntity
import com.t_bank.local.room.entity.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddPageFragment : BaseFragment<FragmentAddPageBinding>(
    R.layout.fragment_add_page,
    FragmentAddPageBinding::inflate
) {

    private val viewModel: AddTransactionViewModel by activityViewModels()

    override fun setupUiOnViewCreated() {

        // Установите дату по умолчанию
        val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        binding.dateText.text = currentDate

        // Обработка нажатия на кнопку календаря
        binding.calendarBtn.setOnClickListener {
            showDatePicker(binding.dateText)
        }

        // Активируйте кнопку add_button только когда все поля заполнены
        val incomeOrExpenseEditText = binding.inputIncomeOrExpenseText
        val categoryNameEditText = binding.inputCategoryNameText
        val amountEditText = binding.inputAmountText
        val commentEditText = binding.inputCommentText
        val addButton = binding.addButton

        fun checkFields() {
            addButton.isEnabled = (incomeOrExpenseEditText.text.toString().isNotEmpty()
                    && categoryNameEditText.text.toString().isNotEmpty()
                    && amountEditText.text.toString().isNotEmpty()
                    && commentEditText.text.toString().isNotEmpty())
        }

        incomeOrExpenseEditText.addTextChangedListener { checkFields() }
        categoryNameEditText.addTextChangedListener { checkFields() }
        amountEditText.addTextChangedListener { checkFields() }
        commentEditText.addTextChangedListener { checkFields() }

        // Обработка нажатия на кнопку add_button
        addButton.setOnClickListener {
            saveTransaction(
                binding.dateText.text.toString(),
                incomeOrExpenseEditText.text.toString(),
                categoryNameEditText.text.toString(),
                amountEditText.text.toString(),
                commentEditText.text.toString()
            )
            findNavController().popBackStack()
        }

        // Установите текст в зависимости от типа транзакции
        val transactionType = viewModel.transactionData.value?.transactionType
        if (transactionType == TransactionType.INCOME) {
            binding.addTransactionTitleText.text = "Новый доход"
            binding.incomeOrExpenseText.text = "Источник дохода"
        } else if (transactionType == TransactionType.EXPENSE) {
            binding.addTransactionTitleText.text = "Новый расход"
            binding.incomeOrExpenseText.text = "Получатель"
        }
    }

    private fun showDatePicker(dateTextView: TextView) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.CustomDatePickerTheme)
            .build()
        datePicker.addOnPositiveButtonClickListener {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(Date(it))
        }
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun saveTransaction(
        date: String,
        source: String,
        category: String,
        amount: String,
        comment: String
    ) {
        val transactionType = viewModel.transactionData.value?.transactionType ?: return
        val dateCreated = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(date) ?: Date()
        val transactionAmount = amount.toDoubleOrNull() ?: 0.0

        val transaction = TransactionEntity(
            dateCreated = dateCreated,
            type = transactionType,
            amount = transactionAmount,
            comment = comment,
            category = category,
            source = source
        )

        viewModel.submitTransaction(transaction)
    }
}