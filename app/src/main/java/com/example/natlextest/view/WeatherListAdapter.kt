package com.example.natlextest.view


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.natlextest.R
import com.example.natlextest.databinding.WeatherListItemBinding
import com.example.natlextest.model.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.time.TimedValue

class WeatherListAdapter @Inject constructor(val context: Context): RecyclerView.Adapter<WeatherListAdapter.Holder>(){

    private val items = ArrayList<Weather>()
    private var allWeather = ArrayList<Weather>()
    private var showCells = false

    fun setItems(items: ArrayList<Weather>) {
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun setAllWeather(items: ArrayList<Weather>){
        this.allWeather.clear()
        this.allWeather.addAll(items)
        this.notifyDataSetChanged()
    }

    fun changeFormat(item: Boolean?) {
        if (item != null) {
            this.showCells = item
        }
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = WeatherListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
        holder.setIsRecyclable(false)
    }

    inner class Holder(private val itemBinding: WeatherListItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        private var cityName: String? = null

        fun bind(item: Weather){
            val itemCounter = allWeather.count { it.cityId == item.cityId }
            val kelvTemp = item.temp
            var fahrTemp = 0.0
            var cellsTemp = 0.0
            if (kelvTemp !== null) {
                cellsTemp = kelvTemp - 273.15
            }
            if (kelvTemp !== null) {
                fahrTemp = (kelvTemp - 273.15) * 9/5 + 32
            }
            if (!showCells) {
                (item.cityName + ", " + fahrTemp.toInt() + " F").also { itemBinding.listCityNameTemp.text = it }
            } else {
                (item.cityName + ", " + cellsTemp.toInt() + " C").also { itemBinding.listCityNameTemp.text = it }
            }
            (item.date + " " + item.time).also { itemBinding.listWeatherDatetime.text = it }
            if (itemCounter > 1) {
                itemBinding.listChartIcon.visibility = View.VISIBLE
            }
            itemBinding.listChartIcon.setOnClickListener {
                cityName = item.cityName
                val intent = Intent(context, ChartActivity::class.java)
                intent.putExtra(context.getString(R.string.city_name_key), cityName)
                context.startActivity(intent)
            }
        }
    }

}