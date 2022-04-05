/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sibedge.corutinesdemo.network


import com.sibedge.corutinesdemo.SkipNetworkInterceptor
import com.sibedge.corutinesdemo.User
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private val service: FetchDataNetwork by lazy {
    val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(SkipNetworkInterceptor())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    retrofit.create(FetchDataNetwork::class.java)
}

fun getFetchNetworkService() = service

/**
 * Main network interface which will fetch a new welcome title for us
 */
interface FetchDataNetwork {
    @GET("users")
    fun fetchUsers(): Call<List<User>>
}


