package com.printful.covid19.model

import com.printful.covid19.viewmodel.CountryData
import com.printful.covid19.viewmodel.VaccinationData
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Factory converter to choose appropriate handler
 */
object CountryDataConverterFactory: Converter.Factory() {
    override fun responseBodyConverter(type: Type, annotations: Array<Annotation?>?, retrofit: Retrofit?): Converter<ResponseBody, List<*>>? {
        return when((type as ParameterizedType).actualTypeArguments[0]){
            CountryData::class.java -> CountryResponseBodyConverter
            VaccinationData::class.java -> VaccinationResponseBodyConverter
            else -> null
        }
    }
}

/**
 * Converter for Country Data
 */
private object CountryResponseBodyConverter : Converter<ResponseBody, List<*>> {
    override fun convert(value: ResponseBody): List<CountryData> {
        val result = mutableListOf<CountryData>()
        value.charStream().readLines().forEach{
            val data = it.split(",")
            result.add(
                    CountryData(
                        data[0],
                        data[1],
                        buildString { for (i in 2..data.lastIndex - 3) append(data[i]) },
                        data[data.lastIndex - 2],
                        data[data.lastIndex - 1],
                        data[data.lastIndex]
                    )
            )
        }
        result.removeAt(0)
        return result
    }
}

/**
 * Converter for Vaccination Data
 */
private object VaccinationResponseBodyConverter : Converter<ResponseBody, List<*>> {
    override fun convert(value: ResponseBody): List<VaccinationData> {
        val result = mutableListOf<VaccinationData>()
        value.charStream().readLines().forEach{
            val data = it.split(",")
            result.add(
                VaccinationData(
                    data[0],
                    data[1],
                    buildString { for (i in 2..data.lastIndex - 4) append(data[i]) },
                    data[data.lastIndex - 3],
                    data[data.lastIndex - 2],
                    data[data.lastIndex - 1],
                    data[data.lastIndex]
                )
            )
        }
        result.removeAt(0)
        return result
    }
}
