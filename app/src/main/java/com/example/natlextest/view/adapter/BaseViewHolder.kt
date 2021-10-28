package com.example.natlextest.view.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<out V : ViewBinding, in I : Item>(
    val binding: V
) : RecyclerView.ViewHolder(binding.root) {

    abstract fun onBind(item: I, listener: ItemClickedListener, allWeather: List<I>, changeUnit: Boolean)
}