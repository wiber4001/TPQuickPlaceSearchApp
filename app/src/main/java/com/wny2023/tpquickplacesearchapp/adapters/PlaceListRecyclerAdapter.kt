package com.wny2023.tpquickplacesearchapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.wny2023.tpquickplacesearchapp.databinding.RecyclerItemListFragmentBinding
import com.wny2023.tpquickplacesearchapp.model.Place

class PlaceListRecyclerAdapter(var context:Context, var documents:MutableList<Place>):Adapter<PlaceListRecyclerAdapter.VH> () {

    inner class VH(var binding:RecyclerItemListFragmentBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding=RecyclerItemListFragmentBinding.inflate(LayoutInflater.from(context),parent,false)
        return VH(binding)
    }

    override fun getItemCount(): Int = documents.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val place:Place =documents[position]
        holder.binding.tvPlaceName.text= place.place_name
        //road_address_name이 ""인 경우가 있음
        // if-else문
//        if(place.road_address_name=="") holder.binding.tvAddress.text=place.address_name
//        else holder.binding.tvAddress.text=place.road_address_name
        // if-else문을 3항연산자 처럼 축약
        holder.binding.tvAddress.text=if(place.road_address_name=="") place.address_name else place.road_address_name
        holder.binding.tvDistance.text="${place.distance} m" //서식모양, 문자열템플릿
    }

}