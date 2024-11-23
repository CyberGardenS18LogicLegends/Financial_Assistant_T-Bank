package com.t_bank.financial_assistant.features.auth_and_reg

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.t_bank.common.BaseFragment
import com.t_bank.common_models.UserDataClass
import com.t_bank.financial_assistant.R
import com.t_bank.financial_assistant.databinding.FragmentAuthenticaneBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthenticateAndRegistrationFragment : BaseFragment<FragmentAuthenticaneBinding>(
    R.layout.fragment_authenticane,
    FragmentAuthenticaneBinding::inflate
) {

    private val viewModel: AuthenticateAndRegistrationViewModel by viewModels()

    override fun setupUiOnViewCreated() {
        binding.authenticateOrRegisterBtn.setOnClickListener {
            viewModel.toggleAuthenticationMode()
        }

        binding.continueBtn.setOnClickListener {
            val userData = UserDataClass(
                login = binding.inputLoginText.text.toString(),
                password = binding.inputPassword.text.toString()
            )
            viewModel.updateUserData(userData)
            viewModel.authenticateOrRegister()
        }

        binding.tIdBtn.setOnClickListener {
            findNavController().navigate(R.id.action_authenticaneFragment_to_homeFragment)
        }

        lifecycleScope.launch {
            viewModel.authenticationMode.collect { mode ->
                updateUiBasedOnMode(mode)
            }
        }

        lifecycleScope.launch {
            viewModel.userData.collect { userData ->
                binding.inputLoginText.setText(userData.login)
                binding.inputPassword.setText(userData.password)
            }
        }

        lifecycleScope.launch {
            viewModel.authResult.collect { result ->
                handleAuthResult(result)
            }
        }
    }

    private fun updateUiBasedOnMode(mode: AuthenticationMode) {
        when (mode) {
            AuthenticationMode.AUTHENTICATION -> {
                binding.title.text = getString(R.string.authentication)
                binding.authenticateOrRegisterBtn.text = getString(R.string.do_register)
                binding.continueBtn.text = getString(R.string.do_auth)
            }
            AuthenticationMode.REGISTRATION -> {
                binding.title.text = getString(R.string.registration)
                binding.authenticateOrRegisterBtn.text = getString(R.string.do_auth)
                binding.continueBtn.text = getString(R.string.register)
            }
        }
    }

    private fun handleAuthResult(result: Result<UserDataClass>) {
        when {
            result.isSuccess -> {
                // Обработка успешной аутентификации или регистрации
                findNavController().navigate(R.id.action_authenticaneFragment_to_homeFragment)
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            }
            result.isFailure -> {
                // Обработка ошибки аутентификации или регистрации
                Toast.makeText(requireContext(), "Error: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}