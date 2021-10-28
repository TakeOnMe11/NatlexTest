package com.example.natlextest.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.natlextest.model.Weather
import java.lang.IllegalArgumentException

class WeatherAdapter(
    private val weatherItem: List<WeatherItem<*, *>>, private val listener: ItemClickedListener
    ): RecyclerView.Adapter<BaseViewHolder<ViewBinding, Item>>() {

    private val items = mutableListOf<Item>()
    private var allWeather = mutableListOf<Item>()
    private var showCells = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<ViewBinding, Item> {
        val inflater = LayoutInflater.from(parent.context)
        return weatherItem.find { it.getLayoutId() == viewType }
            ?.getViewHolder(inflater, parent)
            ?.let { it as BaseViewHolder<ViewBinding, Item> }
            ?: throw IllegalArgumentException("View type not found: $viewType")
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewBinding, Item>, position: Int) {
        holder.onBind(items[position], listener, allWeather, showCells)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return weatherItem.find { it.isRelativeItem(item) }
            ?.getLayoutId()
            ?: throw IllegalArgumentException("View type not found: $item")
    }

    fun setItems(items: List<Weather>) {
        val newList = items.toList()
        val weatherDiffUtil = WeatherDiffUtil(weatherItem, items, newList)
        val diffResult = DiffUtil.calculateDiff(weatherDiffUtil)
        this.items.clear()
        this.items.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
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
}