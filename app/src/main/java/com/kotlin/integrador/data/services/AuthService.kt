package com.kotlin.integrador.data.services

import com.kotlin.integrador.data.model.UserData
import retrofit2.Call
import retrofit2.http.GET

interface AuthService {
    @GET("auth/login/google")
    fun loginWithGoogle(): Call<Void> // Aquí simplemente se inicia la autenticación y se redirige

    @GET("auth/callback/google")
    fun callbackWithGoogle(): Call<UserData> // Esta ruta se usará para recibir los datos después de la autenticación
}