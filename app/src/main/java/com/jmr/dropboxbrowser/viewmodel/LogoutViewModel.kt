package com.jmr.dropboxbrowser.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.domain.usecases.RemoveCredentialsUseCase
import com.jmr.domain.usecases.RevokeTokenUseCase
import com.jmr.dropboxbrowser.util.CoroutineSafeCallHandler
import kotlinx.coroutines.launch

class LogoutViewModel @ViewModelInject constructor(
    private val removeCredentialsUseCase: RemoveCredentialsUseCase,
    private val revokeTokenUseCase: RevokeTokenUseCase
) : ViewModel() {

    private val mutableState: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = mutableState

    fun logUserOut() {
        mutableState.value = State.Loading

        viewModelScope.launch {
            CoroutineSafeCallHandler.call({
                revokeTokenUseCase()
                removeCredentialsUseCase()
            }, {
                mutableState.value = State.Success
            }, {
                mutableState.value = State.Error(it.message)
            })
        }
    }

    sealed class State {
        object Loading : State()
        object Success : State()
        data class Error(val errorMessage: String?) : State()
    }

}