package com.jamian.mytaxi.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamian.mytaxi.network.NetworkCalls
import com.jamian.mytaxi.network.RetrofitClient
import com.jamian.mytaxi.network.TaxiData
import com.jamian.mytaxi.network.TaxiListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class TaxiListRepository{

    private var networkCalls:NetworkCalls? = null

    init {
        networkCalls = RetrofitClient().getNetworkCallList()
    }

    fun getTaxiList(lat1:String,lon1:String,lat2:String,lon2:String):LiveData<ArrayList<TaxiData>>{

        val data = MutableLiveData<ArrayList<TaxiData>>()

        networkCalls?.getTaxiList(lat1,lon1,lat2,lon2)?.enqueue(object : Callback<TaxiListResponse> {
            override fun onFailure(call: Call<TaxiListResponse>, t: Throwable) {
                data.value = ArrayList()
            }

            override fun onResponse(call: Call<TaxiListResponse>, response: Response<TaxiListResponse>) {
                if(response.isSuccessful){
                    data.value = response.body()?.poiList
                }else{
                    data.value = ArrayList()
                }
            }
        })

        return data
    }

    object TaxiListRepositoryProvider{

        fun getTaxiListRepository():TaxiListRepository{
            return TaxiListRepository()
        }

    }
}



