package com.printful.covid19

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity() {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var recyclerView: RecyclerView
    private var skipOnNetworkAvailableAtFirst: Boolean = true
    private val model: Covid19ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupLayoutRefresh()
        setupNetworkListener()
        setupRecyclerView()
        setupSearchView()

        model.getCountryData().observe(this, Observer {
            recyclerView.adapter = RecyclerViewAdapter(it)
        })
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
     * Refresh countries data
     */
    private fun refreshCountryData() {
        if (connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null)
            model.fetchCountriesInformation()
        else
            Toast.makeText(applicationContext, getString(R.string.offline), Toast.LENGTH_SHORT).show()
    }

    /**
     * Setup Swipe Refresh Layout
     */
    private fun setupLayoutRefresh() {
        val refreshLayout:SwipeRefreshLayout = findViewById(R.id.layout_refresh)
        val searchView: SearchView = findViewById<SearchView>(R.id.search_view)
        refreshLayout.setOnRefreshListener {
            refreshCountryData()
            searchView.clearFocus()
            refreshLayout.isRefreshing = false
        }
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
                    refreshCountryData()
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
                if (p0 == null || p0.trim().isEmpty()) {
                    recyclerView.adapter = RecyclerViewAdapter(model.getCountryData().value!!)
                } else {
                    val newList = model.getCountryData().value!!.filter {
                        it.country.startsWith(p0.trim(), ignoreCase = true)
                    }
                    if(newList.isEmpty())
                        Toast.makeText(applicationContext, getString(R.string.wrong_search) , Toast.LENGTH_SHORT).show()
                    else
                        recyclerView.adapter = RecyclerViewAdapter(newList)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })
    }
}