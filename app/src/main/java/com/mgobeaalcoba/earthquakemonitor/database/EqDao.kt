package com.mgobeaalcoba.earthquakemonitor.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.mgobeaalcoba.earthquakemonitor.Earthquake

@Dao
interface EqDao {

    // Metodo para insertar terremotos:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(eqList: MutableList<Earthquake>)

    // Metodo para obtener terremotos
    @Query("SELECT * FROM earthquakes")
    fun getEarthquakes(): MutableList<Earthquake>

    @Query("SELECT * FROM earthquakes ORDER BY magnitude DESC")
    fun getEarthquakeByMagnitude(): MutableList<Earthquake>
}