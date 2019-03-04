package com.jamian.mytaxi.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun Context.isNetworkAvailable(): MutableLiveData<Constants.States> {

    var state = MutableLiveData<Constants.States>()
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetwork = cm.activeNetworkInfo
    if(activeNetwork != null && activeNetwork.isConnectedOrConnecting){
        state.value = Constants.States.NETWORK_AVAILABLE
    }else{
        state.value = Constants.States.NO_NETWORK
    }

    return state
}