package com.mgobeaalcoba.earthquakemonitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mgobeaalcoba.earthquakemonitor.databinding.EqListItemBinding

class EqAdapter : ListAdapter<Earthquake, EqAdapter.EqViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Earthquake>() {
        override fun areItemsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Earthquake, newItem: Earthquake): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EqAdapter.EqViewHolder {
        // El binding en los adapter se carga de forma algo distinta. Cambia el contenido del .inflate():
        val binding = EqListItemBinding.inflate(LayoutInflater.from(parent.context))

        /*
        val view = LayoutInflater.from(parent.context).inflate(R.layout.eq_list_item, parent,
            false)
         */

        // En vez de pasar un view al view holder le voy a pasar el binding
        return EqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EqAdapter.EqViewHolder, position: Int) {
        val earthquake = getItem(position)

        /* Queda en desuso por el funcionamiento del binding
        holder.magnitudeText.text = earthquake.magnitude.toString()
        holder.placeText.text = earthquake.place
         */

        holder.bind(earthquake)
    }

    inner class EqViewHolder(private val binding : EqListItemBinding): RecyclerView.ViewHolder(binding.root) {
        // Mencionamos todas las views que va a mantener o a guardar ese viewHolder que son
        // los dos textView y la imageView no la traemos porque no tenemos que hacer nada con ella.
        // solo la mostramos en cada elemento de la lista.

        /*
        val magnitudeText = view.findViewById<TextView>(R.id.eq_magnitude_text)
        val placeText = view.findViewById<TextView>(R.id.eq_place_text)
         */

        fun bind(earthquake: Earthquake) {
            binding.eqMagnitudeText.text = earthquake.magnitude.toString()
            binding.eqPlaceText.text = earthquake.place
        }
    }
}