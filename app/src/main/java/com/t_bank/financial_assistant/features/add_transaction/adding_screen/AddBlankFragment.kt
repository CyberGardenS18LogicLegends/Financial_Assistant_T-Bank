package com.t_bank.financial_assistant.features.add_transaction.adding_screen

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.t_bank.common.BaseFragment
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.databinding.FragmentAddBlankBinding
import com.t_bank.financial_assistant.features.add_transaction.preview.AddTransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBlankFragment : BaseFragment<FragmentAddBlankBinding>(
    R.layout.fragment_add_blank,
    FragmentAddBlankBinding::inflate
) {

    private val viewModel: AddTransactionViewModel by viewModels()

    override fun setupUiOnViewCreated() {
    }
}