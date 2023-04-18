package com.mgobeaalcoba.earthquakemonitor.main

import android.app.Application
import androidx.lifecycle.*
import com.mgobeaalcoba.earthquakemonitor.Earthquake
import com.mgobeaalcoba.earthquakemonitor.database.getDatabase
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val database = getDatabase(application.applicationContext)
    private val repository = MainRepository(database)

    val eqlist= repository.eqList

    init {
        viewModelScope.launch {
            repository.fetchEarthquakes()
        }
    }
}