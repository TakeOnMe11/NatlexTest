package com.example.natlextest.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.natlextest.R
import com.example.natlextest.model.Weather
import kotlinx.android.synthetic.main.weather_list_item.view.*
import javax.inject.Inject

class WeatherListAdapter @Inject constructor(): RecyclerView.Adapter<WeatherListAdapter.Holder>(){

    private val callback = object: DiffUtil.ItemCallback<Weather>() {
        override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, callback)

    fun submitList(list: List<Weather>) {
        differ.submitList(null)
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_list_item, parent, false) as View
        return Holder(view)
    }

    override fun getItemCount(): Int = differ.currentList.size


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class Holder(v: View): RecyclerView.ViewHolder(v) {
        private var cityName: String? = null

        fun bind(weather: Weather) = with(itemView){
            (weather.cityName + ", " + weather.temp?.toInt() + " " + weather.unit).also { list_city_name_temp.text = it }
            (weather.date + " " + weather.time).also { list_weather_datetime.text = it }
            if (differ.currentList.size > 1) {
                list_chart_icon.visibility = View.VISIBLE
            }
            list_chart_icon.setOnClickListener {
                cityName = weather.cityName
                val intent = Intent(context, ChartActivity::class.java)
                intent.putExtra(context.getString(R.string.city_name_key), cityName)
                context.startActivity(intent)
            }
        }

    }

}