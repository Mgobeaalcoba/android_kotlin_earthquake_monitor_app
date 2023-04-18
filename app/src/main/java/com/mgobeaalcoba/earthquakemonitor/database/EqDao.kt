package com.mgobeaalcoba.earthquakemonitor.database

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

    @Query("SELECT * FROM earthquakes WHERE magnitude > :mag")
    fun getEarthquakeWithMagnitude(mag: Double): MutableList<Earthquake>

    @Update
    fun updateEq(vararg eq: Earthquake)

    @Delete
    fun deleteEq(vararg eq: Earthquake)
}