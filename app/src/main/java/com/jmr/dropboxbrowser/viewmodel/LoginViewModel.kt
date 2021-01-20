package com.jmr.dropboxbrowser.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dropbox.core.android.Auth
import com.jmr.domain.usecases.HasTokenUseCase
import com.jmr.domain.usecases.SaveTokenUseCase

class LoginViewModel @ViewModelInject constructor(
    private val hasTokenUseCase: HasTokenUseCase,
    private val saveTokenUseCase: SaveTokenUseCase) : ViewModel(){

    private val mutableState: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = mutableState

    var isLoginFlow = false

    fun checkTokenStatus(){
        if (!hasTokenUseCase()) {
            val authToken = Auth.getOAuth2Token()
            when{
                !authToken.isNullOrEmpty() && isLoginFlow -> {
                    saveTokenUseCase(authToken)
                    isLoginFlow = false
                    mutableState.value = State.Success
                }
                authToken.isNullOrEmpty() && isLoginFlow -> {
                    isLoginFlow = false
                    mutableState.value = State.Error
                }
            }
        } else {
            mutableState.value = State.Success
        }
    }

    sealed class State {
        object Success : State()
        object Error : State()
    }
}