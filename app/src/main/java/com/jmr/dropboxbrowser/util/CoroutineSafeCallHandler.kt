package com.jmr.dropboxbrowser.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CoroutineSafeCallHandler {

    /**
     * Function that executes the given function on Dispatchers.IO context and switch to Dispatchers.Main context when an error occurs
     * @param callFunction is the function that is returning the wanted object. It must be a suspend function.
     * @param onSuccess is the function to call with the returned value in case of success.
     * @param onError is the function to call when something went wrong.
     */
    suspend inline fun <T> call(crossinline callFunction: suspend () -> T, onSuccess: (response: T) -> Unit, crossinline onError: (error: Throwable) -> Unit) {
        try {
            val responseObject = withContext(Dispatchers.IO) { callFunction.invoke() }
            onSuccess.invoke(responseObject)
        } catch (e: Exception) {
            onError.invoke(e)
        }
    }
}