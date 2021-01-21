package com.jmr.dropboxbrowser.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmr.data.mapper.DropboxFileMapper
import com.jmr.data.model.DropboxFileList
import com.jmr.domain.usecases.GetFilesUseCase
import com.jmr.dropboxbrowser.util.CoroutineSafeCallHandler
import com.jmr.dropboxbrowser.util.FileHelper
import kotlinx.coroutines.launch

class BoxContentViewModel @ViewModelInject constructor(
    private val getFilesUseCase: GetFilesUseCase
) :
    ViewModel() {

    private val mutableState: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = mutableState

    fun getBoxContent(folder: String?) {
        mutableState.value = State.Loading
        viewModelScope.launch {
            CoroutineSafeCallHandler.call({
                getFilesUseCase(folder)
            }, {
                mutableState.value = State.Success(DropboxFileMapper().mapToDataModel(it))
            }, {
                mutableState.value = State.Error(it.message)
            })
        }
    }

    fun getFileIntent(fileName: String, fileUri: Uri) {
        mutableState.value = State.FileIntent(FileHelper().getFileIntent(fileName, fileUri))
    }

    sealed class State {
        object Loading : State()
        data class Success(val files: DropboxFileList) : State()
        data class FileIntent(val intent: Intent): State()
        data class Error(val errorMessage: String?) : State()
    }

}