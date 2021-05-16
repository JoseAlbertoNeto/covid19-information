package com.printful.covid19.model

import com.printful.covid19.viewmodel.CountryData
import com.printful.covid19.viewmodel.VaccinationData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface IDataFetcher{

    @Headers("content-encoding: gzip", "content-type: text/plain; charset=utf-8")
    @GET("locations.csv")
    suspend fun getCountryData(): Response<List<CountryData>>

    @Headers("content-encoding: gzip", "content-type: text/plain; charset=utf-8")
    @GET("country_data/{country}.csv")
    suspend fun getVaccinationData(@Path("country") country: String): Response<List<VaccinationData>>
}