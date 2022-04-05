package com.sibedge.corutinesdemo.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sibedge.corutinesdemo.repository.DataRepository
import com.sibedge.corutinesdemo.repository.DataRepositoryImpl
import com.sibedge.corutinesdemo.User
import kotlinx.coroutines.*

class UsersViewModel : ViewModel() {
    companion object {
        const val TAG = "viewModelScope"
    }

    private val liveData: MutableLiveData<List<User>> = MutableLiveData()
    private val dataRepository: DataRepository = DataRepositoryImpl()

    fun getData(): LiveData<List<User>> = liveData

    init {
        getListOfUses()
    }

    private fun getListOfUses() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = dataRepository.getUsers()
            liveData.postValue(users)
        }
    }
}