package com.example.covid19app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.covid19app.R
import com.example.covid19app.data.Country

class CountryAdapter(private val originalList: List<Country>) :
    RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var filteredList: List<Country> = originalList.toList()

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryNameTextView: TextView = itemView.findViewById(R.id.countryNameTextView)
        val casesTextView: TextView = itemView.findViewById(R.id.casesTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val currentCountry = filteredList[position]
        holder.countryNameTextView.text = currentCountry.country
        holder.casesTextView.text = "Cases: ${currentCountry.cases}"
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.country.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}