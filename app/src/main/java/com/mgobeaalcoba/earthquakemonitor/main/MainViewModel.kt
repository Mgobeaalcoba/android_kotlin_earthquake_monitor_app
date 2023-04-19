package com.mgobeaalcoba.earthquakemonitor.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mgobeaalcoba.earthquakemonitor.Earthquake
import com.mgobeaalcoba.earthquakemonitor.api.ApiResponseStatus
import com.mgobeaalcoba.earthquakemonitor.database.getDatabase
import kotlinx.coroutines.launch
import java.net.UnknownHostException

private val TAG = MainViewModel::class.java.simpleName
class MainViewModel(application: Application): AndroidViewModel(application) {

    private val database = getDatabase(application.applicationContext)
    private val repository = MainRepository(database)

    // Creo mi variable de tipo ApiResponseStatus:
    private val _status = MutableLiveData<ApiResponseStatus>()
    val status: LiveData<ApiResponseStatus>
        get() = _status

    private var _eqList = MutableLiveData<MutableList<Earthquake>>()
    val eqlist: LiveData<MutableList<Earthquake>>
        get() = _eqList

    init {
        reloadEarthquakes(false)
    }

    fun reloadEarthquakes(sortByMagnitude: Boolean) {
        viewModelScope.launch {
            // Si no tengo internet entonces quiero que tome los datos de mi database:
            try {
                _status.value = ApiResponseStatus.LOADING
                _eqList.value = repository.fetchEarthquakes(sortByMagnitude)
                _status.value = ApiResponseStatus.DONE
            } catch (e: UnknownHostException) {
                _status.value = ApiResponseStatus.NOT_INTERNET_CONEXTION
                Log.d(TAG, "No internet conexion")
            }
        }
    }
}