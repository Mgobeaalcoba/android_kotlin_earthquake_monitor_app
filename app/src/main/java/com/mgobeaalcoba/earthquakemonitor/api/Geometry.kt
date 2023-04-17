package com.mgobeaalcoba.earthquakemonitor.api

class Geometry(val coordinates: Array<Double>) {
    // Uso getters para obtener los valores del array que recibo en el JSON.
    val longitude: Double
        get() = coordinates[0]

    val latitude: Double
        get() = coordinates[1]
}
