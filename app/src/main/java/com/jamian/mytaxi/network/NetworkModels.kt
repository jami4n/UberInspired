package com.jamian.mytaxi.network

import java.io.Serializable

data class TaxiData(val id:Int,
                    val coordinate: TaxiCoordinate,
                    val fleetType:String,
                    val heading:Float):Serializable


data class TaxiCoordinate(val latitude: Double,
                           val longitude: Double):Serializable

data class TaxiListResponse(val poiList:ArrayList<TaxiData>)

