package com.example.natlextest.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.natlextest.R
import com.example.natlextest.databinding.WeatherListItemBinding
import com.example.natlextest.model.Weather

class PostWeather: WeatherItem<WeatherListItemBinding, Weather> {
    override fun isRelativeItem(item: Item) = item is Weather

    override fun getLayoutId() = R.layout.weather_list_item

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
    ): BaseViewHolder<WeatherListItemBinding, Weather> {
        val binding = WeatherListItemBinding.inflate(layoutInflater, parent, false)
        return WeatherViewHolder(binding)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<Weather>() {

        override fun areItemsTheSame(oldItem: Weather, newItem: Weather) = oldItem.cityId == newItem.cityId

        override fun areContentsTheSame(oldItem: Weather, newItem: Weather) = oldItem == newItem

    }

}

class WeatherViewHolder(binding: WeatherListItemBinding) : BaseViewHolder<WeatherListItemBinding, Weather>(binding) {

    override fun onBind(item: Weather, listener: ItemClickedListener, allWeather: List<Weather>, changeUnit: Boolean) {
        val itemCounter = allWeather.count { it.cityId == item.cityId }
        if (!changeUnit) {
            (item.cityName + ", " + item.tempFahr?.toInt() + " F").also { binding.listCityNameTemp.text = it }
        } else {
            (item.cityName + ", " + item.tempCells?.toInt() + " C").also { binding.listCityNameTemp.text = it }
        }
        (item.date + " " + item.time).also { binding.listWeatherDatetime.text = it }
        if (itemCounter > 1) {
            binding.listChartIcon.visibility = View.VISIBLE
        }
        binding.listChartIcon.setOnClickListener {
            listener.onClickedItem(item)
        }
    }

}
