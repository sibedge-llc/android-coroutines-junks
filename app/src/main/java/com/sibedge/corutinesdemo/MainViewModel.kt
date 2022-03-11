package com.sibedge.corutinesdemo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel : ViewModel() {
    companion object {
        const val TAG = "viewModelScope"
    }

    private val liveData: MutableLiveData<String> = MutableLiveData("init")

    fun getData(): LiveData<String> = liveData

    private fun startCoroutineOnDispatcher(
        dispatcher: CoroutineDispatcher,
        makeException: Boolean = false
    ) {
        viewModelScope.launch(dispatcher) {
            log("start")
            liveData.postValue(" delay(5000) ")
            delay(5000)

            log("print hi")
            liveData.postValue("---===__hi__===---")

//               long operation which freeze thread
            delay(1000)
            longOperation {
                liveData.postValue("longOperation=> $it")
            }
            log("end")

            launch {
                log("start child")
                log("makeException = $makeException")
                if (makeException) {
                    throw Exception("exception in child")
                }
            }

            sendGet() // - sinch request to the network
        }
    }

    private fun startCoroutineOnDispatcherException(
        dispatcher: CoroutineDispatcher,
        makeExceptionChild: Boolean = false,
        makeExceptionParent: Boolean = false
    ) {
        viewModelScope.launch(dispatcher) {
            log("start")
            liveData.postValue(" delay(5000) ")
            delay(5000)

            log("print hi")
            liveData.postValue("---===__hi__===---")

//               long operation which freeze thread
            delay(1000)
            log("end")

            log("exceptionChild = $makeExceptionChild exceptionParent = $makeExceptionParent")

            launch {
                log("start child")
                if (makeExceptionChild) {
                    throw Exception("exception in child")
                }
                delay(1000)
                sendGet()
            }
            //delay(1000)
            if (makeExceptionParent) {
                throw Exception("exception in parent")
            }
            longOperation {
                liveData.postValue("longOperation=> $it")
            }
        }
    }

    private fun getSuspendRequest() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = suspendOperation()
            liveData.postValue(result)
        }
    }

    private fun networkWithCallback() {
        getNetworkService().fetchNextTitle().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    log("networkWithCallback response = $response")
                    liveData.postValue(response.message())
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    log("networkWithCallback t = $t")
                    liveData.postValue(t.localizedMessage)
                }
            }
        )
    }

    fun start(type: FunctionType) {
        log("clicked $type")
        liveData.postValue(type.javaClass.name)
        when (type) {
            FunctionType.OnMainThread -> startCoroutineOnDispatcher(Dispatchers.Main)
            FunctionType.OnIOThread -> startCoroutineOnDispatcher(Dispatchers.IO)
            FunctionType.Callback -> networkWithCallback()
            FunctionType.Suspend -> getSuspendRequest()
            FunctionType.ChildException -> startCoroutineOnDispatcherException(
                Dispatchers.IO,
                makeExceptionChild = true,
                makeExceptionParent = false
            )
            FunctionType.ParentException -> startCoroutineOnDispatcherException(
                Dispatchers.IO,
                makeExceptionChild = false,
                makeExceptionParent = true
            )
        }
    }
}


sealed class FunctionType {
    object OnMainThread : FunctionType()
    object OnIOThread : FunctionType()
    object Callback : FunctionType()
    object Suspend : FunctionType()
    object ChildException : FunctionType()
    object ParentException : FunctionType()
}


suspend fun suspendOperation(): String {
    return suspendCoroutine { continuation ->
        getNetworkService().fetchNextTitle().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    log("networkWithCallback response = $response")
                    continuation.resume(response.message())
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    log("networkWithCallback t = $t")
                    continuation.resume(t.localizedMessage)
                }
            }
        )
    }
}

fun sendGet() {
    log("sendGet")
    val url = URL("http://www.google.com/")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"  // optional default is GET
        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
        inputStream.bufferedReader().use {
            it.lines().forEach { line ->
                println(line)
                log(line)
            }
        }
    }
    log("endGet")
}

fun longOperation(action: (l: Long) -> Unit) {
    log("start longOperation")
    var n = 0L
    while (n < 1000) {
        Thread.sleep(10) //to freeze thread
        action(n)
        n++
    }
    log("end longOperation")
}

fun log(message: String) {
    val threadName = Thread.currentThread().name
    Log.d(MainViewModel.TAG, "threadName=$threadName, m= $message")
}