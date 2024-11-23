package com.t_bank.financial_assistant.features.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.t_bank.common.BaseFragment
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    R.layout.fragment_home,
    FragmentHomeBinding::inflate
) {

    private val viewModel: HomeViewModel by viewModels()

    override fun setupUiOnViewCreated() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_menu)
        bottomNavigationView.visibility = View.VISIBLE



    }
}