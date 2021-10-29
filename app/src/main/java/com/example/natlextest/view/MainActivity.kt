package com.example.natlextest.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.natlextest.R
import com.example.natlextest.databinding.ActivityMainBinding
import com.example.natlextest.model.Weather
import com.example.natlextest.utils.Resource
import com.example.natlextest.view.adapter.ItemClickedListener
import com.example.natlextest.view.adapter.PostWeather
import com.example.natlextest.view.adapter.WeatherAdapter
import com.example.natlextest.viewmodel.MainViewModel
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.weather_list_item.*
import kotlinx.coroutines.flow.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    lateinit var weatherListAdapter: WeatherAdapter
    private lateinit var mainBinding: ActivityMainBinding
    private var showCells = MutableSharedFlow<Boolean>(replay = 1)
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        initRecyclerView()
        initObservers()
        viewModel.getArrayWeather()
        mainBinding.switchFtoc.setOnCheckedChangeListener { _, p1 -> changeUnits(p1)}
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val item = menu?.findItem(R.id.menuSearch)
        val searchView = item?.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    p0.replace(" ", "")
                    viewModel.getWeatherRemote(p0)
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
                            viewModel.getWeatherRemote(it)
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
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(p0: LocationResult) {
                            viewModel.getWeatherRemote(p0.lastLocation.latitude, p0.lastLocation.longitude)
                            super.onLocationResult(p0)
                        }
                    }
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    fusedLocationClient.requestLocationUpdates(
                        LocationRequest.create(),
                        locationCallback,
                        Looper.getMainLooper()
                    )
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.data.collect {
                onRetrieveData(it)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.error.collect {
                onFailure(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.allWeather.collect {
                onCounterChanged(it)
            }
        }
    }

    private fun changeUnits(flag: Boolean) {
        lifecycleScope.launchWhenStarted {
            showCells.tryEmit(flag)
        }
    }

    private fun onCounterChanged(response: Resource<List<Weather>>?) {
        when(response?.status) {
            Resource.Status.SUCCESS -> weatherListAdapter.setAllWeather(response.data as ArrayList<Weather>)
            Resource.Status.ERROR -> onFailure(response.message)
            else -> onFailure(response?.message)
        }
    }

    private fun initRecyclerView() {
        weatherListAdapter = WeatherAdapter(listOf(PostWeather()), object: ItemClickedListener {
            override fun onClickedItem(item: Weather) {
                val intent = Intent(this@MainActivity, ChartActivity::class.java)
                intent.putExtra(this@MainActivity.getString(R.string.city_name_key), item.cityName)
                this@MainActivity.startActivity(intent)
            }
        })
        with(mainBinding.rvWeatherList) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = weatherListAdapter
        }
    }

    private fun onRetrieveData(response: Resource<List<Weather>>?) {
        when (response?.status) {
            Resource.Status.SUCCESS -> {
                val data = response.data ?: emptyList()
                try{
                    val maxId = data.maxOf { it.id }
                    for (i in data) {
                        if (i.id == maxId) {
                            showCurrentWeather(i)
                            viewModel.getAllWeather()
                        }
                    }
                } catch(e: NoSuchElementException) {
                }
                weatherListAdapter.setItems(data)
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

    private fun showCurrentWeather(weather: Weather) {
        mainBinding.tvCityName.text = weather.cityName
        mainBinding.tvTemp.text = weather.tempFahr?.toInt().toString()
        lifecycleScope.launchWhenStarted {
            showCells.collect {
                if (it) {
                    mainBinding.tvTemp.text = weather.tempCells?.toInt().toString()
                    weatherListAdapter.changeFormat(it)
                } else {
                    mainBinding.tvTemp.text = weather.tempFahr?.toInt().toString()
                    weatherListAdapter.changeFormat(it)
                }
            }
        }

        weather.tempCells.let {
            if (it != null) when {
                it < 10 -> mainBinding.weatherMainWindow.background = AppCompatResources.getDrawable(this, R.color.coldWeather)
                it in 10.0..25.0 -> mainBinding.weatherMainWindow.background = AppCompatResources.getDrawable(this, R.color.normalWeather)
                it > 25 -> mainBinding.weatherMainWindow.background = AppCompatResources.getDrawable(this, R.color.hotWeather)
            }
        }
    }
}