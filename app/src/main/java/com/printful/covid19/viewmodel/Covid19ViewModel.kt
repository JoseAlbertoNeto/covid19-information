package com.printful.covid19.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.printful.covid19.model.DataFetcher
import com.printful.covid19.model.Result

class Covid19ViewModel: ViewModel() {
    /**
     * Get countries that have vaccine information updated at OWID source
     */
    fun fetchCountriesInformation(): LiveData<Result<List<CountryData>?>> = DataFetcher.getCountryData()

    /**
     * Get vaccination information per country in json format from VACCINES_PER_COUNTRY_URL
     * @param countryToSearch Country to search data, like Brazil
     */
    fun fetchVaccinationsPerCountry(countryToSearch: String): LiveData<Result<List<VaccinationData>?>> = DataFetcher.getVaccinationData(countryToSearch)
}