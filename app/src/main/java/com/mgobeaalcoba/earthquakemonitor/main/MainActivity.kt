package com.mgobeaalcoba.earthquakemonitor.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mgobeaalcoba.earthquakemonitor.Earthquake
import com.mgobeaalcoba.earthquakemonitor.R
import com.mgobeaalcoba.earthquakemonitor.api.ApiResponseStatus
import com.mgobeaalcoba.earthquakemonitor.databinding.ActivityMainBinding
import com.mgobeaalcoba.earthquakemonitor.detail.DetailActivity

class MainActivity : AppCompatActivity() {

    // Creo mi variable de ViewModel:
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establezco la relación con mi dataBinding:
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Establezco el tipo de Layout con el que voy a repetir mis elementos en la lista:
        binding.eqRecycler.layoutManager = LinearLayoutManager(this)

        // Creo mi variable de ViewModel:
        viewModel = ViewModelProvider(this, MainViewModelFactory(application)).get(MainViewModel::class.java)

        // Con el objeto adapter creado debo instanciar un adapter:
        val adapter = EqAdapter(this)
        // Asigno el adapter a mi data binding:
        binding.eqRecycler.adapter = adapter

        // Creado mi ViewModel en la MainActivity voy a crear el "observer" para modificar mi
        // activity cuando hayan cambios en los datos de mi LiveData:
        viewModel.eqlist.observe(this, Observer {
            // Podría usar it también en lugar de eqList. Pero es mejor hacerlo así para ser explicitos.
            eqList ->
            adapter.submitList(eqList)

            // Declaramos que queremos mostrar nuestra "Empty view" solo si la lista de earthquakes está vacia.
            // Caso contrario mantenemos su visibilidad en "GONE".
            handleEmptyView(eqList, binding)
        })

        // Creo un observer para el LiveData que maneja los status de mis descargas del servidor de terremotos:
        viewModel.status.observe(this, Observer {
            // a apiResponseStatus la estoy creando en este lambda function:
            apiResponseStatus ->
            if (apiResponseStatus == ApiResponseStatus.LOADING) {
                binding.loadingWheel.visibility = View.VISIBLE
            } else if (apiResponseStatus == ApiResponseStatus.DONE) {
                binding.loadingWheel.visibility = View.GONE
            } else if (apiResponseStatus == ApiResponseStatus.ERROR) {
                binding.loadingWheel.visibility = View.GONE
            }

        })

        // Ya no debo pasar la lista de forma "manual" sino que el observer la pasará frente a cada cambio ocurrido:
        // Le paso al adapter la lista de valores que debe replicar y cargar:
        // adapter.submitList(eqList)

        // Codigo en MainActivity para encender el onClickListener sobre los items de la lista:
        adapter.onItemClickListener = {
            // Toast.makeText(this, it.place, Toast.LENGTH_SHORT).show() // probamos que funcione el on click listener
            // Abro la DetailActivity para mostrar los datos del terremoto que paso.
            openDetailActivity(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.main_menu_sort_magnitude) {
            viewModel.reloadEarthquakes(true)
        } else if (itemId == R.id.main_menu_sort_time){
            viewModel.reloadEarthquakes(false)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openDetailActivity(earthquake: Earthquake) {
        // Creamos un intent activity:
        val intent = Intent(this, DetailActivity::class.java )
        // Solo voy a pasar como putExtra el objeto earthquake aprovechando el Parcelable:
        intent.putExtra(DetailActivity.EARTHQUAKE_KEY, earthquake)
        // Enviamos el objeto matchScore a la siguiente activity:
        startActivity(intent)
    }

    private fun handleEmptyView(
        eqList: MutableList<Earthquake>,
        binding: ActivityMainBinding
    ) {
        if (eqList.isEmpty()) {
            binding.eqEmptyView.visibility = View.VISIBLE
        } else {
            binding.eqEmptyView.visibility = View.GONE
        }
    }
}