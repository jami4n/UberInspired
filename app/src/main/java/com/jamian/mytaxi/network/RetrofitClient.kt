package com.jamian.mytaxi.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient{

    companion object {
        const val MY_TAXI_BASE_URL = "https://fake-poi-api.mytaxi.com"
        //const val MY_TAXI_BASE_URL = "https://raw.githubusercontent.com/jami4n/UberInspired/master/webservice.json/"
    }

    fun getNetworkCallList(): NetworkCalls? {

        var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(MY_TAXI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        return retrofit.create(NetworkCalls::class.java)
    }
}