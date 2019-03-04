package com.jamian.mytaxi.utils

class Constants{

    enum class Filter{
        ALL,
        POOL,
        PERSONAL
    }

    enum class States{
        LOADING,
        NO_NETWORK,
        NETWORK_AVAILABLE,
        SERVER_ERROR,
        OK
    }

    companion object {

        const val FILTER = "filter"
        const val BOUNDS_DEFAULT = "defaultbounds"
        const val TAXI_LIST = "taxidata"
        const val SELECTED_TAXI = "selected_taxi"

    }

}