package com.kotlin.integrador.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

val client = OkHttpClient.Builder().build()

val retrofit = Retrofit.Builder()
    .baseUrl("http://localhost:8080/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()
