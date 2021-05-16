package com.printful.covid19.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.printful.covid19.R
import com.printful.covid19.viewmodel.CountryData

class RecyclerViewAdapter <T> (private val list: List<T>): RecyclerView.Adapter<RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_view_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val countryData =  list[position] as CountryData
        holder.country.text = countryData.country.trim()
        holder.vaccines.text = countryData.vaccines.trim()
        holder.lastDate.text = countryData.lastUpdateDate.trim()
        holder.source.text = countryData.sourceName.trim()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}