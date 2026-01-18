package com.example.aurastore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aurastore.common.Resource
import com.example.aurastore.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<com.example.aurastore.domain.model.User>?>(null)
    val loginState = _loginState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when(event) {
            is LoginEvent.Login -> {
                login(event.email, event.password)
            }
            is LoginEvent.Register -> {
                register(event.email, event.password, event.name)
            }
        }
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val result = authRepository.login(email, password)
            if (result is Resource.Success) {
                _uiEvent.send(UiEvent.NavigateToHome)
            } else {
                _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Login failed"))
            }
            _loginState.value = result
        }
    }

    private fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading()
            val result = authRepository.register(email, password, name)
            if (result is Resource.Success) {
                _uiEvent.send(UiEvent.ShowSnackbar("Welcome to Aura!"))
                _uiEvent.send(UiEvent.NavigateToHome)
            } else {
                _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Registration failed"))
            }
            _loginState.value = null // Reset state if needed, but navigation will happen first
        }
    }

    sealed class UiEvent {
        object NavigateToHome : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    sealed class LoginEvent {
        data class Login(val email: String, val password: String) : LoginEvent()
        data class Register(val email: String, val password: String, val name: String) : LoginEvent()
    }
}
