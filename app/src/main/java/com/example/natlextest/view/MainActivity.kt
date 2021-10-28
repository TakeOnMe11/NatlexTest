package com.example.natlextest.view

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
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
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    lateinit var weatherListAdapter: WeatherAdapter
    private lateinit var mainBinding: ActivityMainBinding
    private var showCells: MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        initRecyclerView()
        initObservers()
        viewModel.getArrayWeather()
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
                    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, object: LocationListener {
                        override fun onLocationChanged(p0: Location) {
                            viewModel.getWeatherRemote(p0.latitude, p0.longitude)
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
                var currentWeather: Weather
                try{
                    val maxId = data.maxOf { it.id }
                    for (i in data) {
                        if (i.id == maxId) {
                            currentWeather = i
                            showCurrentWeather(currentWeather)
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

    private fun <T: Any, L: LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
        liveData.observe(this, Observer(body))

    private fun showCurrentWeather(weather: Weather) {
        mainBinding.tvCityName.text = weather.cityName
        mainBinding.tvTemp.text = weather.tempFahr?.toInt().toString()
        observe(showCells, {
            if (showCells.value == true) {
                mainBinding.tvTemp.text = weather.tempCells?.toInt().toString()
                weatherListAdapter.changeFormat(showCells.value)
            } else {
                mainBinding.tvTemp.text = weather.tempFahr?.toInt().toString()
                weatherListAdapter.changeFormat(showCells.value)
            }
        })

        weather.tempCells.let {
            if (it != null) when {
                it < 10 -> mainBinding.weatherMainWindow.background = AppCompatResources.getDrawable(this, R.color.coldWeather)
                it in 10.0..25.0 -> mainBinding.weatherMainWindow.background = AppCompatResources.getDrawable(this, R.color.normalWeather)
                it > 25 -> mainBinding.weatherMainWindow.background = AppCompatResources.getDrawable(this, R.color.hotWeather)
            }
        }
    }
}