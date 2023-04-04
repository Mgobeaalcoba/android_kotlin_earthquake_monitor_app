package com.mgobeaalcoba.earthquakemonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mgobeaalcoba.earthquakemonitor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establezco la relación con mi dataBinding:
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Establezco el tipo de Layout con el que voy a repetir mis elementos en la lista:
        binding.eqRecycler.layoutManager = LinearLayoutManager(this)

        // Creo una lista vacia donde voy a ir cargando los terremotos que voy a mostrar
        val eqList =  mutableListOf<Earthquake>()

        // Hardcodeo mi lista vacia para que tenga elementos que mostrar:
        eqList.add(Earthquake("1","Buenos Aires",4.3,275349574L, -102.4756, 28.47365))
        eqList.add(Earthquake("2","Lima",2.9,275349574L, -102.4756, 28.47365))
        eqList.add(Earthquake("3","Ciudad de México",6.0,275349574L, -102.4756, 28.47365))
        eqList.add(Earthquake("4","Bogotá",4.1,275349574L, -102.4756, 28.47365))
        eqList.add(Earthquake("5","Caracas",2.5,275349574L, -102.4756, 28.47365))
        eqList.add(Earthquake("6","Madrid",3.3,275349574L, -102.4756, 28.47365))
        eqList.add(Earthquake("7","Acra",6.3,275349574L, -102.4756, 28.47365))

        // Con el objeto adapter creado debo instanciar un adapter:
        val adapter = EqAdapter()
        // Asigno el adapter a mi data binding:
        binding.eqRecycler.adapter = adapter
        // Le paso al adapter la lista de valores que debe replicar y cargar:
        adapter.submitList(eqList)

    }
}