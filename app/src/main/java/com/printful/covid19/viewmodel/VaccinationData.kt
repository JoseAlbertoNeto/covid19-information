package com.printful.covid19.viewmodel

data class VaccinationData(val country: String,
                           val date: String,
                           val vaccine: String,
                           val sourceUrl: String,
                           val totalVaccinations: String,
                           val peopleVaccinated: String,
                           val peopleFullyVaccinated: String
                      )