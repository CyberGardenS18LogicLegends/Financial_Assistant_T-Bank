package com.t_bank.financial_assistant.features.add_transaction.preview

import androidx.fragment.app.viewModels
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.t_bank.common.BaseFragment
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.databinding.FragmentAddTransactionBinding
import com.t_bank.local.room.entity.TransactionType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddTransactionFragment : BaseFragment<FragmentAddTransactionBinding>(
    R.layout.fragment_add_transaction,
    FragmentAddTransactionBinding::inflate
) {

    private val viewModel: AddTransactionViewModel by activityViewModels()

    override fun setupUiOnViewCreated() {


        // Устанавливаем текущую дату по умолчанию
        val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
        binding.dateFromInput.setText(currentDate)
        binding.dateToInput.setText(currentDate)

        binding.dateFromInput.setOnClickListener { showDatePicker(binding.dateFromInput) }
        binding.dateToInput.setOnClickListener { showDatePicker(binding.dateToInput) }

        binding.submitButton.setOnClickListener {
            val dateFrom = binding.dateFromInput.text.toString()
            val dateTo = binding.dateToInput.text.toString()
            val transactionType = if (binding.incomeRadioButton.isChecked) TransactionType.INCOME else TransactionType.EXPENSE
            viewModel.saveTransactionData(dateFrom, dateTo, transactionType)

            // Переход на AddBlankFragment
            findNavController().navigate(R.id.action_addTransactionFragment_to_addBlankFragment)
        }


    }

    private fun showDatePicker(editText: EditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.CustomDatePickerTheme) // Применяем пользовательскую тему
            .build()
        datePicker.addOnPositiveButtonClickListener {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            editText.setText(dateFormat.format(Date(it)))
            editText.context.getColorStateList(R.color.textColorPrimary)
        }
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }


}