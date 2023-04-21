package com.mgobeaalcoba.earthquakemonitor

import android.os.Parcelable
import android.provider.Settings.Global.getString
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@Entity(tableName = "earthquakes")
data class Earthquake(
    @PrimaryKey val id: String,
    val place: String,
    val magnitude: Double,
    val time: Long,
    val longitude: Double,
    val latitude: Double ) : Parcelable {
    // Métodos
        fun getMagnitudeEarthquake(): Double {
        return magnitude
        }
        fun getLatitudeEarthquake(): String {
            return latitude.toString()
        }
        fun getLongitudeEarthquake(): String {
            return longitude.toString()
        }
        fun getPlaceEarthquake(): String {
            return place
        }
        fun getTimestampEarthquake(): String {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(time)
            return simpleDateFormat.format(date)
        }
    }

