package com.jmr.dropboxbrowser.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.v2.DbxClientV2
import com.jmr.data.mapper.SingleDropboxFileMapper
import com.jmr.data.model.FileResponse
import com.jmr.domain.usecases.DownloadFileUseCase
import com.jmr.domain.usecases.GetTokenUseCase
import com.jmr.dropboxbrowser.util.CoroutineSafeCallHandler
import com.jmr.dropboxbrowser.util.Event
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.io.File

class NavHostSharedViewModel @ViewModelInject constructor(
    private val dbxRequestConfig: DbxRequestConfig,
    private val getTokenUseCase: GetTokenUseCase,
    private val downloadFileUseCase: DownloadFileUseCase
) : ViewModel() {

    val dbxClientV2 = DbxClientV2(dbxRequestConfig, getTokenUseCase())
    var picassoInstance: Picasso? = null
    var pendingFile: FileResponse? = null

    private val mutableState: MutableLiveData<Event<State>> = MutableLiveData()
    val state: LiveData<Event<State>> = mutableState

    fun downloadFile(file: FileResponse) {
        pendingFile = null
        mutableState.value = Event(State.Loading)

        viewModelScope.launch {
            CoroutineSafeCallHandler.call({
                downloadFileUseCase(SingleDropboxFileMapper().mapToDomainModel(file))
            }, {
                mutableState.value = Event(State.FileDownloaded(it))
            }, {
                mutableState.value = Event(State.Error(it.message))
            })
        }
    }

    sealed class State {
        object Loading : State()
        data class FileDownloaded(val file: File?) : State()
        data class Error(val errorMessage: String?) : State()
    }

}