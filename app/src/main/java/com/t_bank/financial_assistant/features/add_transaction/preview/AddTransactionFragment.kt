package com.t_bank.financial_assistant.features.add_transaction.preview

import androidx.fragment.app.viewModels
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.t_bank.common.BaseFragment
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.databinding.FragmentAddTransactionBinding
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

    private val viewModel: AddTransactionViewModel by viewModels()

    override fun setupUiOnViewCreated() {
        binding.dateFromInput.setOnClickListener { showDatePicker(binding.dateFromInput) }
        binding.dateToInput.setOnClickListener { showDatePicker(binding.dateToInput) }

        binding.submitButton.setOnClickListener {
            val dateFrom = binding.dateFromInput.text.toString()
            val dateTo = binding.dateToInput.text.toString()
            val isIncome = binding.incomeRadioButton.isChecked
            viewModel.submitTransaction(dateFrom, dateTo, isIncome)
        }

        lifecycleScope.launch {
            viewModel.transactionResult.collect { result ->
                handleTransactionResult(result)
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editText.setText(dateFormat.format(Date(it)))
        }
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun handleTransactionResult(result: Result<Unit>) {
        when {
            result.isSuccess -> {
                Toast.makeText(requireContext(), "Транзакция добавлена", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            result.isFailure -> {
                Toast.makeText(requireContext(), "Ошибка: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}