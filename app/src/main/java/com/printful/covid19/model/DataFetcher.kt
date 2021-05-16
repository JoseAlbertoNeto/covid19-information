package com.printful.covid19.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.printful.covid19.viewmodel.CountryData
import com.printful.covid19.viewmodel.VaccinationData
import retrofit2.*
import java.net.UnknownHostException

object DataFetcher{
    private const val PATH = "https://raw.githubusercontent.com/owid/covid-19-data/master/public/data/vaccinations/"
    private val retrofitClient: Retrofit = Retrofit.Builder()
        .baseUrl(PATH)
        .addConverterFactory(CountryDataConverterFactory)
        .build()

    fun getCountryData(): LiveData<Result<List<CountryData>?>> {
        val endpoint = retrofitClient.create(IDataFetcher::class.java)
        return liveData{
            try {
                val response = endpoint.getCountryData()
                if (response.isSuccessful) {
                    emit(Result.Success(data = response.body()))
                } else {
                    emit(Result.Error(exception = Exception("Failed to get country data")))
                }
            } catch (e: UnknownHostException) {
                emit(Result.Error(exception = UnknownHostException("You are offline, please check your internet connection")))
            } catch (exception: Exception){
                emit(Result.Error(exception = exception))
            }
        }
    }

    fun getVaccinationData(country: String): LiveData<Result<List<VaccinationData>?>>{
        val endpoint = retrofitClient.create(IDataFetcher::class.java)
        return liveData{
            try {
                val response = endpoint.getVaccinationData(country)
                if (response.isSuccessful) {
                    emit(Result.Success(data = response.body()))
                } else {
                    emit(Result.Error(exception = Exception("Failed to get vaccination data")))
                }
            } catch (e: UnknownHostException) {
                emit(Result.Error(exception = UnknownHostException("You are offline, please check your internet connection")))
            } catch (exception: Exception){
                emit(Result.Error(exception = exception))
            }
        }
    }
}