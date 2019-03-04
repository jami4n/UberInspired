package com.jamian.mytaxi.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jamian.mytaxi.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.jamian.mytaxi.adapter.TaxiListAdapter
import com.jamian.mytaxi.network.TaxiData
import com.jamian.mytaxi.utils.Constants
import com.jamian.mytaxi.utils.Constants.*
import com.jamian.mytaxi.utils.Constants.Companion.BOUNDS_DEFAULT
import com.jamian.mytaxi.utils.Constants.Companion.FILTER
import com.jamian.mytaxi.utils.Constants.Companion.SELECTED_TAXI
import com.jamian.mytaxi.utils.Constants.Companion.TAXI_LIST
import com.jamian.mytaxi.viewmodel.TaxiListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.state_handler.*
import kotlinx.android.synthetic.main.toolbar.*

class TaxiListActivity : AppCompatActivity(), TaxiListAdapter.TaxiListener{

    private var taxiListViewModel:TaxiListViewModel? = null
    private var alltaxis:ArrayList<TaxiData> = ArrayList()
    private var taxis:ArrayList<TaxiData> = ArrayList()
    private var adapter:TaxiListAdapter? = null
    private var selectedFilter = Filter.ALL.name
    //private var defaultBounds = Bounds("53.694865","9.757589","53.394655","10.099891")
    private var defualtLatLong2= LatLng(53.694865,9.757589);
    private var defualtLatLong1 = LatLng(53.394655,10.099891);
    private var defaultBounds = LatLngBounds(defualtLatLong1,defualtLatLong2)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = TaxiListAdapter(taxis,this,this)
        recyc_taxi.layoutManager = LinearLayoutManager(this)
        recyc_taxi.adapter = adapter

        recyc_taxi.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy > 0 ){
                    fab_maps.hide()
                }else if(dy < 0){
                    fab_maps.show()
                }
            }
        })

        fab_maps.setOnClickListener {

            var animationDuration:Long = 600
            animateFab(animationDuration)

            Handler().postDelayed({

                openMapsActivity()

            }, animationDuration - 100)

        }

        taxiListViewModel = ViewModelProviders.of(this).get(TaxiListViewModel::class.java)


        tv_pool_taxis.setOnClickListener {
            filterVehicles(Filter.POOL.name)
        }

        tv_personal_taxis.setOnClickListener {
            filterVehicles(Filter.PERSONAL.name)
        }

        tv_all_taxis.setOnClickListener {
            filterVehicles(Filter.ALL.name)
        }



        taxiListViewModel!!.states?.observe(this, Observer {

            when(it){
                Constants.States.NO_NETWORK -> {
                    Glide.with(this).load(R.drawable.ic_brake).into(iv_state_image)
                    tv_state_text.text = resources.getString(R.string.no_internet)
                    state_handler.visibility = VISIBLE
                }
                Constants.States.NETWORK_AVAILABLE -> {
                    state_handler.visibility = GONE
                    iv_state_image.setImageDrawable(null)
                    observeTaxiList(savedInstanceState)
                }

                Constants.States.SERVER_ERROR -> {
                    Glide.with(this).load(R.drawable.ic_brake).into(iv_state_image)
                    tv_state_text.text = resources.getString(R.string.server_error)
                    state_handler.visibility = VISIBLE
                }

                Constants.States.LOADING -> {
                    Glide.with(this).load(R.drawable.ic_tire).into(iv_state_image)
                    tv_state_text.text = resources.getString(R.string.loading)
                    state_handler.visibility = VISIBLE
                }

                Constants.States.OK -> {
                    iv_state_image.setImageDrawable(null);
                    state_handler.visibility = GONE
                }
            }
        })


        if(savedInstanceState != null){
            observeTaxiList(savedInstanceState)
        }




    }

    private fun openMapsActivity(selectedTaxi:TaxiData? = null, overrideTransaction:Boolean = true) {

        var i = Intent(this@TaxiListActivity,MapsActivity::class.java)
        i.putExtra(BOUNDS_DEFAULT,defaultBounds)
        i.putExtra(TAXI_LIST,alltaxis)
        i.putExtra(SELECTED_TAXI,selectedTaxi)
        startActivity(i)

        if(overrideTransaction)
        overridePendingTransition(0, 0)
    }

    private fun observeTaxiList(savedInstanceState: Bundle?) {

        taxiListViewModel!!.states?.value = Constants.States.LOADING

        taxiListViewModel!!
            .getAllTaxis(defaultBounds.northeast.latitude.toString(),defaultBounds.northeast.longitude.toString(),defaultBounds.southwest.latitude.toString(),defaultBounds.southwest.longitude.toString())
            .observe(this, Observer {
                displayTaxisInList(it)

                if(it.size > 0){
                    taxiListViewModel!!.states?.value = Constants.States.OK

                    if(savedInstanceState != null){
                        filterVehicles(savedInstanceState.getString(FILTER, Filter.ALL.name)!!)
                    }

                }else{
                    taxiListViewModel!!.states?.value = Constants.States.SERVER_ERROR
                }


            })
    }


    private fun animateFab(duration:Long){
        var s = ScaleAnimation(1f,25f,1f,25f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        s.duration = duration
        s.fillAfter = false
        s.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                Handler().postDelayed({
                    fab_maps.setImageResource(R.drawable.ic_map)
                },200)
            }

            override fun onAnimationStart(animation: Animation?) {
                fab_maps.setImageResource(android.R.color.transparent)
            }

        })
        fab_maps.startAnimation(s)
    }

    private fun filterVehicles(filter:String){

        selectedFilter = filter

        when(filter){

            Filter.ALL.name ->{

                indicator_personal.visibility = View.GONE
                indicator_pooling.visibility = View.GONE
                indicator_all.visibility = View.VISIBLE
                tv_all_taxis.setTextColor(resources.getColor(R.color.colorWhite))
                tv_personal_taxis.setTextColor(resources.getColor(R.color.colorTextGrey))
                tv_pool_taxis.setTextColor(resources.getColor(R.color.colorTextGrey))

                taxis.clear()
                taxis.addAll(alltaxis)
                adapter?.notifyDataSetChanged()
            }
            Filter.POOL.name ->{
                indicator_personal.visibility = View.GONE
                indicator_all.visibility = View.GONE
                indicator_pooling.visibility = View.VISIBLE
                tv_all_taxis.setTextColor(resources.getColor(R.color.colorTextGrey))
                tv_personal_taxis.setTextColor(resources.getColor(R.color.colorTextGrey))
                tv_pool_taxis.setTextColor(resources.getColor(R.color.colorWhite))

                taxis.clear()
                taxis.addAll(alltaxis.filter { it.fleetType.equals("POOLING") })
                adapter?.notifyDataSetChanged()
            }
            Filter.PERSONAL.name ->{
                indicator_all.visibility = View.GONE
                indicator_pooling.visibility = View.GONE
                indicator_personal.visibility = View.VISIBLE
                tv_all_taxis.setTextColor(resources.getColor(R.color.colorTextGrey))
                tv_personal_taxis.setTextColor(resources.getColor(R.color.colorWhite))
                tv_pool_taxis.setTextColor(resources.getColor(R.color.colorTextGrey))

                taxis.clear()
                taxis.addAll(alltaxis.filter { it.fleetType.equals("TAXI") })
                adapter?.notifyDataSetChanged()
            }

        }

    }

    private fun displayTaxisInList(list:ArrayList<TaxiData>){

        fab_maps.show()

        taxis.clear()
        alltaxis.clear()
        taxis.addAll(list)
        alltaxis.addAll(list)
        adapter?.notifyDataSetChanged();
    }

    override fun onTaxiClicked(position: Int, taxi: TaxiData) {
        Handler().postDelayed({
            openMapsActivity(taxi,false)
        },500)


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(FILTER, selectedFilter);
    }

}
