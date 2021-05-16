package com.printful.covid19.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.printful.covid19.viewmodel.Covid19ViewModel
import com.printful.covid19.R
import com.printful.covid19.model.Result
import com.printful.covid19.viewmodel.CountryData


class MainActivity : AppCompatActivity() {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var countriesDataList: List<CountryData>
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private var skipOnNetworkAvailableAtFirst: Boolean = true
    private val model: Covid19ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupLayoutRefresh()
        setupNetworkListener()
        setupLoading()
        setupRecyclerView()
        setupSearchView()
        updateCountriesData()
    }

    override fun onDestroy() {
        deregisterNetworkListener()
        skipOnNetworkAvailableAtFirst = true
        super.onDestroy()
    }

    /**
     * Deregister network status callback
     */
    private fun deregisterNetworkListener() {
        if(this::connectivityManager.isInitialized && this::networkCallback.isInitialized)
            connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    /**
     * Setup Swipe Refresh Layout
     */
    private fun setupLayoutRefresh() {
        val refreshLayout:SwipeRefreshLayout = findViewById(R.id.layout_refresh)
        val searchView: SearchView = findViewById(R.id.search_view)
        refreshLayout.setOnRefreshListener {
            updateCountriesData()
            searchView.clearFocus()
            refreshLayout.isRefreshing = false
        }
    }

    /**
     * Setup progress bar to show loading data status
     */
    private fun setupLoading(){
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
    }

    /**
     * Listen network status to ensure connectivity and fetch data
     */
    private fun setupNetworkListener() {
        connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if(!skipOnNetworkAvailableAtFirst){
                    this@MainActivity.runOnUiThread{updateCountriesData()}
                    Toast.makeText(applicationContext, getString(R.string.online), Toast.LENGTH_SHORT).show()
                }
                skipOnNetworkAvailableAtFirst = false
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Toast.makeText(applicationContext, getString(R.string.offline), Toast.LENGTH_SHORT).show()
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    /**
     * Find element and configure recycler view
     */
    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_covid)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    /**
     * Setup search view and its listeners
     */
    private fun setupSearchView() {
        val searchView: SearchView = findViewById(R.id.search_view)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (this@MainActivity::countriesDataList.isInitialized){
                    if (p0 == null || p0.trim().isEmpty()) {
                        recyclerView.adapter =
                            RecyclerViewAdapter(
                                countriesDataList
                            )
                    } else {
                        val newList = countriesDataList.filter {
                            it.country.startsWith(p0.trim(), ignoreCase = true)
                        }
                        if(newList.isEmpty())
                            Toast.makeText(applicationContext, getString(R.string.wrong_search) , Toast.LENGTH_SHORT).show()
                        else
                            recyclerView.adapter =
                                RecyclerViewAdapter(
                                    newList
                                )
                    }
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if ((p0 == null || p0.trim().isEmpty()) && (this@MainActivity::countriesDataList.isInitialized
                            && countriesDataList.size != (recyclerView.adapter?.itemCount ?: 0))) {
                    recyclerView.adapter =
                        RecyclerViewAdapter(
                            countriesDataList
                        )
                }
                return true
            }
        })
    }

    private fun updateCountriesData(){
        progressBar.visibility = View.VISIBLE
        model.fetchCountriesInformation().observe(this, Observer {
            progressBar.visibility = View.GONE
            it?.let{ response ->
                when(response) {
                    is Result.Success -> {
                        if (response.data != null){
                            countriesDataList = response.data
                            recyclerView.adapter =
                                RecyclerViewAdapter(
                                    response.data
                                )
                        }
                    }
                    is Result.Error -> Toast.makeText(this, response.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}