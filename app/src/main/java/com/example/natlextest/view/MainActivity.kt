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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.natlextest.R
import com.example.natlextest.model.Weather
import com.example.natlextest.utils.Resource
import com.example.natlextest.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.weather_list_item.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    @Inject
    lateinit var weatherListAdapter: WeatherListAdapter

    private var locationManager: LocationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initRecyclerView()
        initObservers()
        viewModel.getArrayWeather()
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
                    locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
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
    }

    private fun initRecyclerView() {
        val viewManager = LinearLayoutManager(this@MainActivity)
        val listAdapter = WeatherListAdapter()
        rv_weather_list.apply {
            layoutManager = viewManager
            adapter = listAdapter
            setHasFixedSize(true)
        }
    }

    private fun onRetrieveData(response: Resource<List<Weather>>?) {
        when (response?.status) {
            Resource.Status.SUCCESS -> {
                val data = response.data ?: emptyList()
                weatherListAdapter.submitList(data)
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
}