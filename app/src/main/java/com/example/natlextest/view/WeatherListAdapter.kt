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

class WeatherListAdapter(private val listener: ItemClickedListener): RecyclerView.Adapter<WeatherListAdapter.Holder>(){

    private val items = ArrayList<Weather>()
    private var allWeather = ArrayList<Weather>()
    private var showCells = false

    interface ItemClickedListener {
        fun onClickedItem(item: Weather)
    }

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
        return Holder(binding, listener)
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position], listener)
        holder.setIsRecyclable(false)

    }

    inner class Holder(private val itemBinding: WeatherListItemBinding, private val listener: ItemClickedListener): RecyclerView.ViewHolder(itemBinding.root){

        fun bind(item: Weather, listener: ItemClickedListener){
            val itemCounter = allWeather.count { it.cityId == item.cityId }
            if (!showCells) {
                (item.cityName + ", " + item.tempFahr?.toInt() + " F").also { itemBinding.listCityNameTemp.text = it }
            } else {
                (item.cityName + ", " + item.tempCells?.toInt() + " C").also { itemBinding.listCityNameTemp.text = it }
            }
            (item.date + " " + item.time).also { itemBinding.listWeatherDatetime.text = it }
            if (itemCounter > 1) {
                itemBinding.listChartIcon.visibility = View.VISIBLE
            }
            itemBinding.listChartIcon.setOnClickListener {
                listener.onClickedItem(item)
            }
        }

    }

}