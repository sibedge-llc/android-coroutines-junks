package com.sibedge.corutinesdemo.repository

import com.sibedge.corutinesdemo.User
import com.sibedge.corutinesdemo.log
import com.sibedge.corutinesdemo.network.getFetchNetworkService

interface DataRepository {
    suspend fun getUsers(): List<User>
}

class DataRepositoryImpl : DataRepository {
    private val network = getFetchNetworkService()

    override suspend fun getUsers(): List<User> {
        val resp = network.fetchUsers().execute()

        log("resp = $resp")

        return if (resp.isSuccessful) {
            resp.body() ?: throw NetworkException.RespNullError
        } else {
            throw NetworkException.RespError
        }
    }

}

sealed class NetworkException : Throwable() {
    object RespError : NetworkException()
    object RespNullError : NetworkException()
}