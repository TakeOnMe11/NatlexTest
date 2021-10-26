package com.example.natlextest.view

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.natlextest.R
import com.example.natlextest.databinding.ActivityMainBinding
import com.example.natlextest.model.Weather
import com.example.natlextest.utils.Resource
import com.example.natlextest.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CoroutineScope {

    private var job: Job = Job()

    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var weatherListAdapter: WeatherListAdapter

    private var locationManager: LocationManager? = null
    private lateinit var mainBinding: ActivityMainBinding
    private var showCells: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        initRecyclerView()
        initObservers()
        launch {
            viewModel.getArrayWeather()
        }
        mainBinding.switchFtoc.setOnCheckedChangeListener { _, p1 -> showCells.postValue(p1)}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val item = menu?.findItem(R.id.menuSearch)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    p0.replace(" ", "")
                    launch {
                        viewModel.getWeatherRemote(p0)
                    }
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSearch -> {
                val searchView: SearchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        p0?.let {
                            it.replace(" ", "")
                            launch {
                                viewModel.getWeatherRemote(it)
                            }
                        }
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        return true
                    }
                })
            }
            R.id.geopos -> {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, object: LocationListener {
                        override fun onLocationChanged(p0: Location) {
                            launch {
                                viewModel.getWeatherRemote(p0.latitude, p0.longitude)
                            }
                        }

                        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

                        override fun onProviderEnabled(provider: String) {}

                        override fun onProviderDisabled(provider: String) {}

                    } )
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initObservers() {
        observe(viewModel.data, ::onRetrieveData)
        observe(viewModel.error, ::onFailure)
        observe(viewModel.allWeather, ::onCounterChanged)
    }

    private fun onCounterChanged(response: Resource<List<Weather>>?) {
        when(response?.status) {
            Resource.Status.SUCCESS -> weatherListAdapter.setAllWeather(response.data as ArrayList<Weather>)
            Resource.Status.ERROR -> onFailure(response.message)
            else -> onFailure(response?.message)
        }
    }

    private fun initRecyclerView() {
        weatherListAdapter = WeatherListAdapter(this)
        mainBinding.rvWeatherList.layoutManager = LinearLayoutManager(this)
        mainBinding.rvWeatherList.adapter = weatherListAdapter
    }

    private fun onRetrieveData(response: Resource<List<Weather>>?) {
        when (response?.status) {
            Resource.Status.SUCCESS -> {
                val data = response.data ?: emptyList()
                var currentWeather: Weather
                try{
                    val maxId = data.maxOf { it.id }
                    for (i in data) {
                        if (i.id == maxId) {
                            currentWeather = i
                            showCurrentWeather(currentWeather)
                            launch {
                                currentWeather.cityName?.let { viewModel.getAllWeather() }
                            }
                        }
                    }
                } catch(e: NoSuchElementException) {
                }
                weatherListAdapter.setItems(data as ArrayList<Weather>)
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

    private fun showCurrentWeather(weather: Weather) {
        mainBinding.tvCityName.text = weather.cityName
        val kelvTemp = weather.temp
        var fahrTemp = 0.0
        var cellsTemp = 0.0
        if (kelvTemp !== null) {
            cellsTemp = kelvTemp - 273.15
        }
        if (kelvTemp !== null) {
            fahrTemp = (kelvTemp - 273.15) * 9/5 + 32
        }
        mainBinding.tvTemp.text = fahrTemp.toInt().toString()
        observe(showCells, {
            if (showCells.value == true) {
                mainBinding.tvTemp.text = cellsTemp.toInt().toString()
                weatherListAdapter.changeFormat(showCells.value)
            } else {
                mainBinding.tvTemp.text = fahrTemp.toInt().toString()
                weatherListAdapter.changeFormat(showCells.value)
            }
        })
        when {
            cellsTemp < 10 -> mainBinding.weatherMainWindow.background = AppCompatResources.getDrawable(this, R.color.coldWeather)
            cellsTemp in 10.0..25.0 -> mainBinding.weatherMainWindow.background = AppCompatResources.getDrawable(this, R.color.normalWeather)
            cellsTemp > 25 -> mainBinding.weatherMainWindow.background = AppCompatResources.getDrawable(this, R.color.hotWeather)
        }
    }
}