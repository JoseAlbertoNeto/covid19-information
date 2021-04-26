package com.printful.covid19

data class CountryData(val country:String,
                       val isoCode: String,
                       val vaccines:String,
                       val lastUpdateDate: String,
                       val sourceName: String,
                       val sourceWebsite: String
                       )