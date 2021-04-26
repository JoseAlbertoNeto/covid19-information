package com.printful.covid19

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.net.UnknownHostException

class Covid19ViewModel: ViewModel() {
    companion object {
        private const val LOG_MESSAGE = "Covid19TrustedData"
        private const val LOCATIONS_URL = "https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/vaccinations/locations.csv"
        private const val VACCINES_PER_COUNTRY_URL = "https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/vaccinations/country_data/"
    }

    private var countryToSearch = ""

    private val countries: MutableLiveData<List<CountryData>> by lazy {
        MutableLiveData<List<CountryData>>().also {
            fetchCountriesInformation()
        }
    }

    private val vaccinePerCountry: MutableLiveData<List<VaccinationData>> by lazy {
        MutableLiveData<List<VaccinationData>>().also {
            fetchVaccinationsPerCountry(countryToSearch)
        }
    }

    /**
     * Get countries that have vaccine information updated at OWID source
     */
    fun fetchCountriesInformation() {
        GlobalScope.launch {
            try {
                val result = mutableListOf<CountryData>()
                val connection: URLConnection = URL(LOCATIONS_URL).openConnection()
                connection.connect()
                (connection.content as InputStream).reader().forEachLine {
                    val data = it.split(",")
                    result.add(CountryData(data[0], data[1], buildString {for(i in 2..data.lastIndex-3) append(data[i]) }, data[data.lastIndex-2] , data[data.lastIndex-1], data[data.lastIndex]))
                }
                result.removeAt(0)
                countries.postValue(result)
            } catch (e: IOException) {
                Log.e(LOG_MESSAGE, e.toString())
            } catch (e: MalformedURLException) {
                Log.e(LOG_MESSAGE, e.toString())
            } catch (e: UnknownHostException) {
                Log.e(LOG_MESSAGE, "You are offline, please check your internet connection")
            }
        }
    }

    /**
     * Get vaccination information per country in json format from VACCINES_PER_COUNTRY_URL
     * @param countryToSearch Country to search data, like Brazil
     */
    fun fetchVaccinationsPerCountry(countryToSearch: String) {
        GlobalScope.launch {
            try {
                val connection: URLConnection = URL("$VACCINES_PER_COUNTRY_URL${countryToSearch}.csv").openConnection()
                connection.connect()
                val result = mutableListOf<VaccinationData>()
                (connection.content as InputStream).reader().forEachLine {
                    val data = it.split(",")
                    result.add(VaccinationData(data[0], data[1], buildString {for(i in 2..data.lastIndex-4) append(data[i]) }, data[data.lastIndex-3], data[data.lastIndex-2], data[data.lastIndex-1], data[data.lastIndex]))
                }
                result.removeAt(0)
                vaccinePerCountry.postValue(result)
            } catch (e: IOException) {
                Log.e(LOG_MESSAGE, e.toString())
            } catch (e: MalformedURLException) {
                Log.e(LOG_MESSAGE, e.toString())
            } catch (e: UnknownHostException) {
                Log.e(LOG_MESSAGE, "You are offline, please check your internet connection")
            }
        }
    }

    /**
     * Get countries data
     */
    fun getCountryData(): LiveData<List<CountryData>> {
        return countries
    }

    /**
     * Get vaccine per country
     */
    fun getVaccinePerCountry(country: String): LiveData<List<VaccinationData>> {
        countryToSearch = country
        return vaccinePerCountry
    }
}