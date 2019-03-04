package com.jamian.mytaxi.network

import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NetworkCalls{

    @GET("/")
    fun getTaxiList(@Query("p1Lat") position1Latitude:String,
                    @Query("p1Lon") position1Longitude:String,
                    @Query("p2Lat") position2Latitude:String,
                    @Query("p2Lon") position2Longitude:String): Call<TaxiListResponse>


}