package com.sibedge.corutinesdemo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

    private fun startCoroutineOnMainThread() {
        viewModelScope.launch(Dispatchers.Main) {
            Log.d(TAG, "start on MAIN")
            delay(5000)

            Log.d(TAG, "print hi from Main")
            liveData.postValue("hi")

//               long operation which freeze mainThread

            longOperation {
                liveData.postValue("hi from Main=> $it")
            }
            Log.d(TAG, "print hi from Main")

//            sendGet() // - sinch request to the network
        }
    }

    private fun startCoroutineOnIOThread() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "start in IO")
            delay(2000)
            liveData.postValue("hi from Dispatchers.IO")
            Log.d(TAG, "hi from Dispatchers.IO")

//            sendGet() // - sinch request to the network
            longOperation {
                liveData.postValue("from Dispatchers.IO => $it")
            }
        }
    }


    private fun getSuspendRequest() {
//        val result = suspendOperation()

        viewModelScope.launch(Dispatchers.IO) {
            val result = suspendOperation()
            liveData.postValue(result)
        }
    }

    private fun networkWithCallback() {
        getNetworkService().fetchNextTitle().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d(TAG, "networkWithCallback response = $response")
                    liveData.postValue(response.message())
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, "networkWithCallback t = $t")
                    liveData.postValue(t.localizedMessage)
                }
            }
        )
    }

    fun start(type: FunctionType) {
        Log.d(TAG, "clicked $type")
        liveData.postValue(type.javaClass.name)
        when (type) {
            FunctionType.OnMainThread -> startCoroutineOnMainThread()
            FunctionType.OnIOThread -> startCoroutineOnIOThread()
            FunctionType.Callback -> networkWithCallback()
            FunctionType.Suspend -> getSuspendRequest()
        }
    }
}


sealed class FunctionType {
    object OnMainThread : FunctionType()
    object OnIOThread : FunctionType()
    object Callback : FunctionType()
    object Suspend : FunctionType()
}


suspend fun suspendOperation(): String {
    return suspendCoroutine { continuation ->
        getNetworkService().fetchNextTitle().enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d(MainViewModel.TAG, "networkWithCallback response = $response")
                    continuation.resume(response.message())
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(MainViewModel.TAG, "networkWithCallback t = $t")
                    continuation.resume(t.localizedMessage)
                }
            }
        )
    }
}

fun sendGet() {
    val url = URL("http://www.google.com/")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"  // optional default is GET
        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
        inputStream.bufferedReader().use {
            it.lines().forEach { line ->
                println(line)
            }
        }
    }
}

fun longOperation(action: (l: Long) -> Unit) {
    Log.d(MainViewModel.TAG, "start longOperation")
    var n = 0L
    while (n < 1000) {
        Thread.sleep(10) //to freeze mainThread
        action(n)
        n++
    }
    Log.d(MainViewModel.TAG, "end longOperation")
}