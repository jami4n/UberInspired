package com.jamian.mytaxi.adapter

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.jamian.mytaxi.R
import com.jamian.mytaxi.network.TaxiData
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.each_taxi_item.view.*
import java.util.*
import java.util.logging.Handler

class TaxiListAdapter(var taxis:ArrayList<TaxiData>, var context: Context, var listener: TaxiListener): RecyclerView.Adapter<TaxiListAdapter.MYVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MYVH {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.each_taxi_item,parent,false)
        return MYVH(v)
    }

    override fun getItemCount(): Int {
        return taxis.size
    }

    override fun onBindViewHolder(holder: MYVH, position: Int) {
        var taxi = taxis[position]
        holder.car_id.text = "HHK " + taxi.id

        if(taxi.fleetType.equals("POOLING")){
            holder.title.text = context.resources.getString(R.string.pooling_available)
            Glide.with(context).load(R.drawable.ic_share).into(holder.icon)
        }else{
            holder.title.text = context.resources.getString(R.string.hire_available)
            Glide.with(context).load(R.drawable.ic_taxi).into(holder.icon)
        }

        holder.location.text = ""

        Single.create<String> {

            try {
                var cordinates = LatLng(taxi.coordinate.latitude, taxi.coordinate.longitude)
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(cordinates.latitude, cordinates.longitude, 1)
                var locality = addresses[0].locality

                if (!addresses[0].subLocality.isNullOrEmpty())
                    locality = addresses[0].subLocality + "," + addresses[0].locality

                it.onSuccess(locality)

            }catch (e:Exception){

            }

        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:SingleObserver<String>{
                override fun onSuccess(t: String) {
                    holder.location.text = t
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }

            })


        holder.taxiView.setOnClickListener {


            var s = ScaleAnimation(1f,10f,1f,10f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f)
            s.duration = 1000
            s.fillAfter = false
            holder.shape.startAnimation(s)

            listener.onTaxiClicked(position,taxi)
        }

    }


    class MYVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.tv_title
        var location = itemView.tv_location
        var car_id = itemView.tv_car_id
        var icon = itemView.iv_taxi_icon
        var shape = itemView.v_shape

        var taxiView = itemView
    }

    interface TaxiListener{
        fun onTaxiClicked(position: Int, taxi: TaxiData)
    }
}