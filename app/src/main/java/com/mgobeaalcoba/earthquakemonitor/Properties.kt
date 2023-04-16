package com.mgobeaalcoba.earthquakemonitor

import com.squareup.moshi.Json

class Properties(@Json(name = "mag") val magnitude: Double, val place: String, val time: Long)
