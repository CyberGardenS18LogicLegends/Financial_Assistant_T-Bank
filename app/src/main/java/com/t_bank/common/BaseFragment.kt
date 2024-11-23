package com.t_bank.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding>(
    @LayoutRes layoutRes: Int,
    private val bindingInflater : (inflater: LayoutInflater) -> VB
) : Fragment(layoutRes){

    private var _binding: VB? = null
    val binding: VB by lazy {  _binding as VB  }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUiOnViewCreated()
    }


    abstract fun setupUiOnViewCreated()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}