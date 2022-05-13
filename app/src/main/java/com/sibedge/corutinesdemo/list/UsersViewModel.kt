package com.sibedge.corutinesdemo.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibedge.corutinesdemo.User
import com.sibedge.corutinesdemo.repository.DataRepository
import com.sibedge.corutinesdemo.repository.DataRepositoryImpl
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*

class UsersViewModel : ViewModel() {

    private val liveData: MutableLiveData<List<User>> = MutableLiveData()
    private val liveDataJobStates: MutableLiveData<JobsState> = MutableLiveData()
    private val liveDataExcText: MutableLiveData<String> = MutableLiveData()
    private val dataRepository: DataRepository = DataRepositoryImpl()
    private var loadJob: Job? = null
    private var postJob: Job? = null
    private var addJob1: Job? = null
    private var addJob2: Job? = null
    private var companyJob: Job? = null
    private var watchDog: Job? = null
    private var exceptionLevel = 0
    private var shouldThrowException = false
    private var isWorking = true

    private val handler = CoroutineExceptionHandler { ctx, exception ->
        Timber.d("coroutineContext =$ctx exception = $exception")
        liveDataExcText.postValue(exception.message)
    }

    fun getData(): LiveData<List<User>> = liveData
    fun getState(): LiveData<JobsState> = liveDataJobStates
    fun getExcInfo(): LiveData<String> = liveDataExcText

    init {
        getListOfUsers()
        watchDog = viewModelScope.launch(SupervisorJob()) {
            while (isWorking) {
                delay(1000)
                liveDataJobStates.postValue(
                    JobsState(
                        Status.onState(loadJob),
                        Status.onState(postJob),
                        Status.onState(companyJob),
                        Status.onState(addJob1),
                        Status.onState(addJob2)
                    )
                )
            }
        }
    }

    private fun getListOfUsers() {
        loadJob = viewModelScope.launch(Dispatchers.IO + handler) {
            shouldThrowException(0, shouldThrowException)
            val users = dataRepository.getUsers()

            companyJob = launch {
                users.forEach { user ->
                    val userId = user.id
                    delay(1000L)
                    user.myCompany = "Sibedge_$userId"
                    liveData.postValue(users)
                    Timber.d("for user $userId myCompany = ${user.myCompany}")
                    shouldThrowException(1, shouldThrowException)
                }
                addJob1 = launch {
                    var count = 0
                    repeat(users.size) {
                        delay(100L)
                        count++
                        shouldThrowException(2, shouldThrowException)
                    }
                    Timber.d("addJob1 counted ->$count")
                }
            }
            postJob = launch {
                users.forEach { user ->
                    shouldThrowException(1, shouldThrowException)
                    delay(1000)
                    val userId = user.id
                    val posts = dataRepository.getPosts(userId)
                    user.postCounts = Random().nextInt(posts.size) + 1
                    liveData.postValue(users)
                    Timber.d("for user $userId posts.c = ${user.postCounts}")
                }
                addJob2 = launch {
                    var count = 0
                    repeat(users.size) {
                        delay(100L)
                        count++
                        shouldThrowException(2, shouldThrowException)
                    }
                    Timber.d("addJob2 counted ->$count")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        isWorking = false
    }

    private fun shouldThrowException(currentLevel: Int, should: Boolean) {
        if (should && exceptionLevel == currentLevel)
            throw Exception("manual exception")
    }

    fun reloadData() {
        liveData.value = emptyList()
        getListOfUsers()
    }

    fun erasExc() {
        exceptionLevel = 10
        loadJob = null
        postJob = null
        addJob1 = null
        addJob2 = null
        companyJob = null
    }

    fun cancelGeneralJob() {
        loadJob?.cancel()
    }

    fun cancelCompanyJob() {
        companyJob?.cancel()
    }

    fun cancelPostJob() {
        postJob?.cancel()
    }

    fun throwExceptionGeneral() {
        exceptionLevel = 0
        shouldThrowException = true
    }

    fun throwExceptionPost() {
        exceptionLevel = 1
        shouldThrowException = true
    }

    fun throwExceptionCompanyJob() {
        exceptionLevel = 1
        shouldThrowException = true
    }

    fun throwExceptionAddJob() {
        exceptionLevel = 2
        shouldThrowException = true
    }

    fun restartPostJob() {
        postJob?.start()
    }
}

data class JobsState(
    val loadJobState: Status,
    val posJobState: Status,
    val companyJobState: Status,
    val addJob1: Status,
    val addJob2: Status
)

enum class Status {
    Undefined,
    Active,
    Cancelled,
    Completed;

    companion object {
        fun onState(job: Job?): Status {
            if (job == null) return Undefined
            return with(job) {
                when {
                    isActive -> Active
                    isCancelled -> Cancelled
                    isCompleted -> Completed
                    else -> Undefined
                }
            }
        }
    }
}