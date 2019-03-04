package com.jamian.mytaxi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jamian.mytaxi.model.TaxiListRepository
import com.jamian.mytaxi.network.TaxiData
import com.jamian.mytaxi.utils.Constants
import com.jamian.mytaxi.utils.isNetworkAvailable

class TaxiListViewModel(application: Application) : AndroidViewModel(application) {

    var taxis:LiveData<ArrayList<TaxiData>>? = null
    var states:MutableLiveData<Constants.States>? = application.isNetworkAvailable()

    fun getAllTaxis(lat1:String,lon1:String,lat2:String,lon2:String) : LiveData<ArrayList<TaxiData>>{

        if(taxis == null){
            this.taxis = TaxiListRepository.TaxiListRepositoryProvider.getTaxiListRepository().getTaxiList(lat1,lon1,lat2,lon2)
        }
        return taxis!!
    }
}