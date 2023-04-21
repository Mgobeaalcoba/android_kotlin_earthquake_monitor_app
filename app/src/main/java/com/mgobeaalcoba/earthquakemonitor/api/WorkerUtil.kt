package com.mgobeaalcoba.earthquakemonitor.api

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkerUtil {
    fun scheduleSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Ponemos como condición que esté conectado a internet
            .setRequiresBatteryNotLow(true) // También que no tenga batería baja
            .build()

        val syncRequest =
            PeriodicWorkRequestBuilder<SyncWorkManager>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SyncWorkManager.WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, syncRequest
        )
    }
}