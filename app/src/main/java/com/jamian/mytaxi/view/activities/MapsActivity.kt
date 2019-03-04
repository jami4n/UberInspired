package com.jamian.mytaxi.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.jamian.mytaxi.R
import com.jamian.mytaxi.network.TaxiData

import com.jamian.mytaxi.utils.Constants.Companion.BOUNDS_DEFAULT
import com.jamian.mytaxi.utils.Constants.Companion.SELECTED_TAXI
import com.jamian.mytaxi.utils.Constants.Companion.TAXI_LIST
import android.location.Geocoder
import java.util.*




class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    override fun onMarkerClick(marker: Marker?): Boolean {

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker?.position,16f))
        marker?.showInfoWindow()

        return true
    }


    private lateinit var googleMap: GoogleMap
    private lateinit var defaultBounds:LatLngBounds
    private lateinit var taxis:ArrayList<TaxiData>
    private var selectedTaxi:TaxiData? = null

    private var markers = ArrayList<Marker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        defaultBounds = intent.extras.getParcelable(BOUNDS_DEFAULT) as LatLngBounds
        taxis = intent.extras.getSerializable(TAXI_LIST) as ArrayList<TaxiData>

        if(intent.extras.getSerializable(SELECTED_TAXI) != null){
            selectedTaxi = intent.extras.getSerializable(SELECTED_TAXI) as TaxiData
        }


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        googleMap.setOnMarkerClickListener(this);

        Handler().postDelayed({
            setDefaultMapBounds()
        },500)

    }

    private fun setDefaultMapBounds() {

        val bounds = LatLngBounds.builder().include(defaultBounds.northeast).include(defaultBounds.southwest).build()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,0))
        displayTaxisOnMap(taxis)

    }


    private fun displayTaxisOnMap(taxis: ArrayList<TaxiData>?) {

        clearMarkers()

        for(t in taxis!!){
            var cordinates = LatLng(t.coordinate.latitude,t.coordinate.longitude)

            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(cordinates.latitude, cordinates.longitude, 1)
            var locality = addresses[0].locality

            if(!addresses[0].subLocality.isNullOrEmpty())
                locality = addresses[0].subLocality + "," + addresses[0].locality

            var marker = googleMap.addMarker(MarkerOptions().position(cordinates).title(locality))
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car))
            marker.isFlat = true
            marker.rotation = t.heading
            markers.add(marker)
        }

        if(selectedTaxi != null){
            displaySelectedTaxi()
        }

    }

    private fun displaySelectedTaxi() {
        var cordinates = LatLng(selectedTaxi!!.coordinate.latitude,selectedTaxi!!.coordinate.longitude)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cordinates,16f))
    }

    private fun clearMarkers(){
        for (m in markers)
            m.remove()
    }

}
