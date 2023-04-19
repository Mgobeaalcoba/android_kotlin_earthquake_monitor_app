package com.mgobeaalcoba.earthquakemonitor.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mgobeaalcoba.earthquakemonitor.Earthquake
import com.mgobeaalcoba.earthquakemonitor.R
import com.mgobeaalcoba.earthquakemonitor.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EARTHQUAKE_KEY = "earthquake"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Levantamos el objeto que nos trajimos de MainActivity
        val bundle = intent.extras!!

        // Reconstruyo el objeto de clase Earthquake para usarlo:
        val earthquake = bundle.getParcelable<Earthquake>(EARTHQUAKE_KEY)!!

        // Especificamos de que objeto son los atributos que vamos a mostrar en el layout:
        binding.earthquake = earthquake

    }
}