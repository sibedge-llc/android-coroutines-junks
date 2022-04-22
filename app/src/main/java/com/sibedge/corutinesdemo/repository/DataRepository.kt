package com.sibedge.corutinesdemo.repository

import com.sibedge.corutinesdemo.Post
import com.sibedge.corutinesdemo.User
import com.sibedge.corutinesdemo.log
import com.sibedge.corutinesdemo.network.getFetchNetworkService

interface DataRepository {
    suspend fun getUsers(): List<User>

    suspend fun getPosts(userId: Int): List<Post>
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

    override suspend fun getPosts(userId: Int): List<Post> {
        val resp = network.fetchPosts().execute()

        log("resp = $resp")

        return if (resp.isSuccessful) {
            sortUserPosts(userId, resp.body())
        } else {
            throw NetworkException.RespError
        }
    }

    private fun sortUserPosts(userId: Int, list: List<Post>?): List<Post> {
        val res = mutableListOf<Post>()
        list?.forEach {
            if (it.userId == userId) {
                res.add(it)
            }
        }
        return res
    }
}

sealed class NetworkException : Throwable() {
    object RespError : NetworkException()
    object RespNullError : NetworkException()
}