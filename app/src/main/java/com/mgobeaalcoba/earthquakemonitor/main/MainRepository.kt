package com.mgobeaalcoba.earthquakemonitor.main

import com.mgobeaalcoba.earthquakemonitor.Earthquake
import com.mgobeaalcoba.earthquakemonitor.api.EqJsonResponse
import com.mgobeaalcoba.earthquakemonitor.api.service
import com.mgobeaalcoba.earthquakemonitor.database.EqDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository (private val database: EqDatabase) {

    suspend fun fetchEarthquakes(sortByMagnitude: Boolean): MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {

            // Obtengo los datos de terremotos desde mi Servidor.
            val eqJsonResponse = service.getLastHourEarthquakes()
            // Envío los datos obtenidos a parsear para poder construir mis objetos
            val eqList = parseEqResult(eqJsonResponse)

            // Abro mi database e inserto mis datos traidos desde el servidor
            database.eqDao.insertAll(eqList)

            fetchEarthquakesFromDb(sortByMagnitude)
        }
    }

    private suspend fun fetchEarthquakesFromDb(sortByMagnitude: Boolean): MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
            if (sortByMagnitude) {
                database.eqDao.getEarthquakeByMagnitude()
            } else {
                database.eqDao.getEarthquakes()
            }
        }
    }

    private fun parseEqResult(eqJsonResponse: EqJsonResponse): MutableList<Earthquake> {
        // Armo la lista vacia que luego de completar voy a devolver:
        val eqList = mutableListOf<Earthquake>()

        // Obtengo los distintos features de mi eqJsonResponse
        val featureList = eqJsonResponse.features

        // Ahora convertimos nuestro objeto EqJsonResponse en Terremotos así:
        for (feature in featureList) {
            val id = feature.id
            val place = feature.properties.place
            val magnitude = feature.properties.mag
            val time = feature.properties.time
            val longitude = feature.geometry.longitude
            val latitude = feature.geometry.latitude
            val earthquake = Earthquake(id, place, magnitude, time, longitude, latitude)
            eqList.add(earthquake)
        }
        return eqList
    }
}