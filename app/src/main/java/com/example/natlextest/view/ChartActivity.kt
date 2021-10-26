package com.example.natlextest.view

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.natlextest.R
import com.example.natlextest.databinding.ActivityChartBinding
import com.example.natlextest.model.Weather
import com.example.natlextest.utils.Resource
import com.example.natlextest.viewmodel.MainViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.weather_list_item.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*
import org.w3c.dom.Entity

@AndroidEntryPoint
class ChartActivity: AppCompatActivity(), CoroutineScope {

    private var job: Job = Job()
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityChartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContentView(binding.root)
        val keyCityName = intent.getStringExtra(getString(R.string.city_name_key))
        initObservers()
        launch {
            keyCityName?.let { viewModel.getWeatherLocal(it) }
        }
    }

    private fun initObservers() {
        observe(viewModel.data, ::onRetrieveData)
        observe(viewModel.error, ::onFailure)
    }

    private fun onRetrieveData(response: Resource<List<Weather>>?) {
        when (response?.status) {
            Resource.Status.SUCCESS -> {
                val data = response.data ?: emptyList()
                showTemp(data)
                showGraph(data)
            }
            Resource.Status.LOADING -> onLoading(true)
            Resource.Status.ERROR -> onFailure(response.message)
            else -> onFailure(response?.message)
        }
    }

    private fun onLoading(b: Boolean) {

    }

    private fun onFailure(error: String?) {

    }

    private fun <T: Any, L: LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
        liveData.observe(this, Observer(body))

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private fun showTemp(weather: List<Weather>?){
        val tempMin = ArrayList<Double>()
        val tempMax = ArrayList<Double>()
        if (weather != null) {
            for (i in weather.indices) {
                weather[i].tempMin?.let { tempMin.add(it) }
            }
            for (i in weather.indices) {
                weather[i].tempMax?.let { tempMax.add(it) }
            }
            binding.tempMinValue.text = tempMin.minOf { it }.toString()
            binding.tempMaxValue.text = tempMax.minOf { it }.toString()
        }
    }

    private fun showGraph(weather: List<Weather>?) {
        val temp = ArrayList<Double>()
        val result = ArrayList<Entry>()
        val dataSet: ArrayList<ILineDataSet> = ArrayList()
        val days = ArrayList<String>()
        if (weather != null) {
            for (i in weather.indices) {
                weather[i].tempMin?.let { temp.add(it) }
            }
            for (i in weather.indices) {
                weather[i].date?.let { days.add(it) }
            }
            for (i in weather.indices) {
                result.add(Entry(i.toFloat(), temp[i].toFloat()))
            }
        }
        val resultLineDataSet = LineDataSet(result, "")
        dataSet.add(resultLineDataSet)
        val chartData = LineData(dataSet)

        val formatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return days[value.toInt()]
            }
        }
        val xAxis = binding.graphView.xAxis
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = 70f
        xAxis.valueFormatter = formatter
        binding.graphView.data = chartData
    }
}