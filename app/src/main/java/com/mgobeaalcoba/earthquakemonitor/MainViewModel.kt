package com.mgobeaalcoba.earthquakemonitor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class MainViewModel: ViewModel() {
    // Creo mi job y mi coroutineScope para trabajar con corutinas y que mi app no trone:
    // private val job = Job()
    // private val coroutineScope = CoroutineScope(Dispatchers.Main+ job)

    // Migramos la lista de terremotos como Mutable live data:
    private var _eqList = MutableLiveData<MutableList<Earthquake>>()

    // Creamos también el LiveData que sea "pareja" de nuestro mutable live data de arriba
    // Esta variable es la que voy a observar desde el main activity para que cuando cambie pinte
    // los cambios ocurridos en nuestra activity:
    val eqlist: LiveData<MutableList<Earthquake>>
        get() = _eqList

    init {
        // Lanzo la corrutina principal y dentro de ella una secundaria del tipo IO para armar mi lista.
        // La corrutina secundaria debe devolverle a la corrutina primaria la lista de terremotos al
        // finalizar su ejecución.
        viewModelScope.launch {
            _eqList.value = fetchEarthquakes()
        }
    }

    private suspend fun fetchEarthquakes(): MutableList<Earthquake> {
        return withContext(Dispatchers.IO) {
            val eqList =  mutableListOf<Earthquake>()

            // Hardcodeo mi lista vacia para que tenga elementos que mostrar:
            eqList.add(Earthquake("1","Buenos Aires",4.3,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("2","Lima",2.9,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("3","Ciudad de México",6.0,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("4","Bogotá",4.1,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("5","Caracas",2.5,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("6","Madrid",3.3,275349574L, -102.4756, 28.47365))
            eqList.add(Earthquake("7","Acra",6.3,275349574L, -102.4756, 28.47365))

            eqList
        }
    }

//    override fun onCleared() {
//        super.onCleared()
//        job.cancel()
//    }

}