package com.printful.covid19.ui

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.printful.covid19.viewmodel.Covid19ViewModel
import com.printful.covid19.R
import com.printful.covid19.model.Result
import com.printful.covid19.viewmodel.VaccinationData
import java.text.NumberFormat
import java.util.*

class MoreInformationActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private val model: Covid19ViewModel by viewModels()
    private lateinit var country: String
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_information)
        setupLayoutRefresh()
        setupLoading()
        connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        country = intent.getSerializableExtra("country").toString()
        fetchVaccineInformation()
    }

    /**
     * Observe vaccination information
     */
    private fun fetchVaccineInformation(){
        progressBar.visibility = View.VISIBLE
        model.fetchVaccinationsPerCountry(country).observe(this, Observer{
            it?.let {
                    response ->
                when(response){
                    is Result.Success -> {
                        if (response.data != null) {
                            updateVaccineInformation(response.data[response.data.lastIndex])
                            progressBar.visibility = View.GONE
                        }
                    }
                    is Result.Error -> Toast.makeText(this, response.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
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
            textView.text = "${NumberFormat.getIntegerInstance(Locale.getDefault()).format(data.toLong())} ${getString(
                R.string.doses
            )}"
        }
    }

    /**
     * Setup Swipe Refresh Layout
     */
    private fun setupLayoutRefresh(){
        val refreshLayout: SwipeRefreshLayout = findViewById(R.id.refresh_layout_more_information_activity)
        refreshLayout.setOnRefreshListener {
            fetchVaccineInformation()
            refreshLayout.isRefreshing = false
        }
    }

    /**
     * Setup progress bar to show loading data status
     */
    private fun setupLoading(){
        progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.VISIBLE
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