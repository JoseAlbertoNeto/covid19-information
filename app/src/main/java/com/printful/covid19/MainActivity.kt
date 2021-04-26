package com.printful.covid19

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
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

        model.getCountryData().observe(this, Observer{
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
    private fun deregisterNetworkListener(){
        if(this::connectivityManager.isInitialized && this::networkCallback.isInitialized)
            connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    /**
     * Refresh countries data
     */
    private fun refreshCountryData(){
        if (connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null)
            model.fetchCountriesInformation()
        else
            Toast.makeText(applicationContext, getString(R.string.offline), Toast.LENGTH_SHORT).show()
    }

    /**
     * Setup Swipe Refresh Layout
     */
    private fun setupLayoutRefresh(){
        val refreshLayout:SwipeRefreshLayout = findViewById(R.id.layout_refresh)
        refreshLayout.setOnRefreshListener {
            refreshCountryData()
            refreshLayout.isRefreshing = false
        }
    }

    /**
     * Listen network status to ensure connectivity and fetch data
     */
    private fun setupNetworkListener(){
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
    private fun setupRecyclerView(){
        recyclerView = findViewById(R.id.recycler_view_covid)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}