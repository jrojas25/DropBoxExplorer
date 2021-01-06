package com.jmr.dropboxbrowser.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.data.mapper.DropboxFileMapper
import com.jmr.data.model.DropboxFileList
import com.jmr.domain.usecases.GetFilesUseCase
import kotlinx.coroutines.launch
import java.lang.Exception

class BoxContentViewModel @ViewModelInject constructor(
    private val getFilesUseCase: GetFilesUseCase
) :
    ViewModel() {

    private val mutableState: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = mutableState

    fun getBoxContent(folder: String?) {
        mutableState.value = State.Loading
        try {
            viewModelScope.launch {
                mutableState.value =
                    State.Success(DropboxFileMapper().mapToDataModel(getFilesUseCase(folder)))
            }
        } catch (e: Exception) {
            mutableState.value = State.Error(e.message)
        }
    }

    sealed class State {
        object Loading : State()
        data class Success(val files: DropboxFileList) : State()
        data class Error(val errorMessage: String?) : State()
    }

}