package com.mgobeaalcoba.earthquakemonitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.eq_list_item, parent,
            false)
        return EqViewHolder(view)
    }

    override fun onBindViewHolder(holder: EqAdapter.EqViewHolder, position: Int) {
        val earthquake = getItem(position)
        holder.magnitudeText.text = earthquake.magnitude.toString()
        holder.placeText.text = earthquake.place
    }

    inner class EqViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        // Mencionamos todas las views que va a mantener o a guardar ese viewHolder que son
        // los dos textView y la imageView no la traemos porque no tenemos que hacer nada con ella.
        // solo la mostramos en cada elemento de la lista.
        val magnitudeText = view.findViewById<TextView>(R.id.eq_magnitude_text)
        val placeText = view.findViewById<TextView>(R.id.eq_place_text)
    }
}