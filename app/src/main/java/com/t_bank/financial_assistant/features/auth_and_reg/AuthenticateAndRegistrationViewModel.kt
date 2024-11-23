package com.t_bank.financial_assistant.features.auth_and_reg

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t_bank.common_models.UserDataClass
import com.t_bank.local.sharedpreferences.SharedPreferenceManager
import com.t_bank.network.auth_and_reg.AuthenticationAPI
import com.t_bank.network.auth_and_reg.RegistrationAPI
import com.t_bank.network.auth_and_reg.request_models.Request
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticateAndRegistrationViewModel @Inject constructor(
    private val sharedPreferenceManager: SharedPreferenceManager,
    private val authenticationAPI: AuthenticationAPI,
    private val registrationAPI: RegistrationAPI
) : ViewModel() {

    private val _authenticationMode = MutableStateFlow(AuthenticationMode.AUTHENTICATION)
    val authenticationMode: StateFlow<AuthenticationMode> get() = _authenticationMode

    private val _userData = MutableStateFlow(sharedPreferenceManager.getUserData())
    val userData: StateFlow<UserDataClass> get() = _userData

    private val _authResult = MutableSharedFlow<Result<UserDataClass>>()
    val authResult: SharedFlow<Result<UserDataClass>> get() = _authResult

    init {
        loadUserData()
    }

    private fun loadUserData() {
        _userData.value = sharedPreferenceManager.getUserData()
    }

    fun toggleAuthenticationMode() {
        _authenticationMode.value = when (_authenticationMode.value) {
            AuthenticationMode.AUTHENTICATION -> AuthenticationMode.REGISTRATION
            AuthenticationMode.REGISTRATION -> AuthenticationMode.AUTHENTICATION
        }
    }

    fun authenticateOrRegister() {
        val userData = Request(
            username = _userData.value.login,
            password = _userData.value.password
        )
        when (_authenticationMode.value) {
            AuthenticationMode.AUTHENTICATION -> authenticate(userData)
            AuthenticationMode.REGISTRATION -> register(userData)
        }
    }

    private fun authenticate(userData: Request) {
        viewModelScope.launch {
            try {
                val result = authenticationAPI.authenticate(userData)
                Log.d("AuthenticateAndRegistrationViewModel", "result: ${result.toString()}")
                _authResult.emit(Result.success(result))
                sharedPreferenceManager.saveAuthData(result)
            } catch (e: Exception) {
                _authResult.emit(Result.failure(e))
            }
        }
    }

    private fun register(userData: Request) {
        viewModelScope.launch {
            try {
                val result = registrationAPI.registration(userData)
                _authResult.emit(Result.success(result))
                sharedPreferenceManager.saveAuthData(result)
            } catch (e: Exception) {
                _authResult.emit(Result.failure(e))
            }
        }
    }

    fun updateUserData(userData: UserDataClass) {
        _userData.value = userData
        sharedPreferenceManager.saveAuthData(userData)
    }
}



enum class AuthenticationMode {
    AUTHENTICATION, REGISTRATION
}