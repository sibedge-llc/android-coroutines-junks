package com.sibedge.corutinesdemo.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibedge.corutinesdemo.User
import com.sibedge.corutinesdemo.log
import com.sibedge.corutinesdemo.repository.DataRepository
import com.sibedge.corutinesdemo.repository.DataRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.util.*

class UsersViewModel : ViewModel() {
    companion object {
        const val TAG = "viewModelScope"
    }

    private val liveData: MutableLiveData<List<User>> = MutableLiveData()
    private val dataRepository: DataRepository = DataRepositoryImpl()
    private lateinit var loadJob: Job
    private lateinit var postJob: Job
    private lateinit var companyJob: Job
    private var exceptionLevel = 0
    private var shouldThrowException = false

    fun getData(): LiveData<List<User>> = liveData

    init {
        getListOfUses()
    }

    private fun getListOfUses() {
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            shouldThrowException(0, shouldThrowException)
            val users = dataRepository.getUsers()

            companyJob = launch {
                shouldThrowException(1, shouldThrowException)
                users.forEach { user ->
                    val userId = user.id
                    delay(1000L)
                    user.myCompany = "Sibedge_$userId"
                    liveData.postValue(users)
                    Timber.d("for user $userId myCompany = ${user.myCompany}")
                }
            }
            postJob = launch {
                users.forEach { user ->
                    shouldThrowException(2, shouldThrowException)
                    delay(1000)
                    val userId = user.id
                    val posts = dataRepository.getPosts(userId)
                    user.postCounts = Random().nextInt(posts.size) + 1
                    liveData.postValue(users)
                    Timber.d("for user $userId posts.c = ${user.postCounts}")
                }
            }
        }
    }

    private fun shouldThrowException(currentLevel: Int, should: Boolean) {
        if (should && exceptionLevel == currentLevel)
            throw Exception("manual exception")
    }

    fun reloadData() {
        liveData.value = emptyList()
        getListOfUses()
    }

    fun cancelGeneralJob() {
        loadJob.cancel()
    }

    fun cancelCompanyJob() {
        companyJob.cancel()
    }

    fun cancelPostJob() {
        postJob.cancel()
    }

    fun throwException() {
        exceptionLevel = 2
        shouldThrowException = true
    }
}