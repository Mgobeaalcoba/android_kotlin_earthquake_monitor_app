package com.mgobeaalcoba.earthquakemonitor.main

import android.app.Application
import androidx.lifecycle.*
import com.mgobeaalcoba.earthquakemonitor.Earthquake
import com.mgobeaalcoba.earthquakemonitor.database.getDatabase
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    private var _eqList = MutableLiveData<MutableList<Earthquake>>()
    val eqlist: LiveData<MutableList<Earthquake>>
        get() = _eqList

    private val database = getDatabase(application.applicationContext)
    private val repository = MainRepository(database)

    init {
        viewModelScope.launch {
            _eqList.value = repository.fetchEarthquakes()
        }
    }
}