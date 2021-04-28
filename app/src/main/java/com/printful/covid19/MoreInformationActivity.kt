package com.printful.covid19

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.text.NumberFormat
import java.util.*

class MoreInformationActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private val model: Covid19ViewModel by viewModels()
    private lateinit var country: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_information)
        setupLayoutRefresh()
        connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        country = intent.getSerializableExtra("country").toString()

        model.getVaccinePerCountry(country).observe(this, Observer{
            updateVaccineInformation(it[it.lastIndex])
        })
    }

    /**
     * Refresh vaccination for a given country
     */
    private fun refreshCountryData(){
        if (connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null)
            model.fetchVaccinationsPerCountry(country)
        else
            Toast.makeText(applicationContext, getString(R.string.offline), Toast.LENGTH_SHORT).show()
    }

    /**
     * Handler function for updateVaccineInformation
     * @param textView
    DecimalFormat()     * @param data
     */
    private fun setTextAndColor(textView:TextView, data:String){
        if(data.isEmpty()) {
            textView.setTextColor(resources.getColor(R.color.colorRed))
            textView.text = getString(R.string.information_not_available)
        } else {
            textView.text = "${NumberFormat.getIntegerInstance(Locale.getDefault()).format(data.toLong())} ${getString(R.string.doses)}"
        }
    }

    /**
     * Setup Swipe Refresh Layout
     */
    private fun setupLayoutRefresh(){
        val refreshLayout: SwipeRefreshLayout = findViewById(R.id.refresh_layout_more_information_activity)
        refreshLayout.setOnRefreshListener {
            refreshCountryData()
            refreshLayout.isRefreshing = false
        }
    }

    /**
     * Update vaccine information in UI
     * @param vaccinationData
     */
    private fun updateVaccineInformation(vaccinationData: VaccinationData){
        findViewById<TextView>(R.id.countryInputTextView).text = vaccinationData.country
        findViewById<TextView>(R.id.vaccinesInputTextView).text = vaccinationData.vaccine
        findViewById<TextView>(R.id.lastObservationInputView).text = vaccinationData.date
        findViewById<TextView>(R.id.sourceInputView2).text = vaccinationData.sourceUrl
        setTextAndColor(findViewById(R.id.firstDoseInputView), vaccinationData.peopleVaccinated)
        setTextAndColor(findViewById(R.id.secondDoseInputView), vaccinationData.peopleFullyVaccinated)
        setTextAndColor(findViewById(R.id.totalDosesInputView), vaccinationData.totalVaccinations)
    }
}