package com.example.natlextest.view.adapter

import com.example.natlextest.model.Weather

interface ItemClickedListener {
    fun onClickedItem(item: Weather)
}