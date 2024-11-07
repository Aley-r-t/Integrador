package com.kotlin.integrador.data.auth

import com.kotlin.integrador.data.model.UserData
import com.kotlin.integrador.data.network.retrofit
import com.kotlin.integrador.data.services.AuthService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun authenticateWithGoogle() {
    val authService = retrofit.create(AuthService::class.java)

    // Llamada para iniciar la autenticación con Google
    authService.loginWithGoogle().enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                println("Redirigiendo al usuario a la autenticación de Google...")
            } else {
                println("Error al iniciar la autenticación: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            println("Error de red: ${t.message}")
        }
    })
}

fun receiveGoogleCallback() {
    val authService = retrofit.create(AuthService::class.java)

    // Llamada para recibir los datos de la autenticación después de la redirección
    authService.callbackWithGoogle().enqueue(object : Callback<UserData> {
        override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
            if (response.isSuccessful) {
                val userData = response.body()
                println("Usuario autenticado: ${userData?.name}, Email: ${userData?.email}")
            } else {
                println("Error al recibir los datos de la autenticación: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<UserData>, t: Throwable) {
            println("Error de red: ${t.message}")
        }
    })
}
