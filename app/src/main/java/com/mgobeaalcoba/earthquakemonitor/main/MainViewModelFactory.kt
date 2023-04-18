package com.mgobeaalcoba.earthquakemonitor.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// En el constructor del ViewModelFactory y en el return debe ir aquello que necesita mi ViewModel
// En este caso solo "application". Pero podría sumar otras necesidades.
class MainViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }
