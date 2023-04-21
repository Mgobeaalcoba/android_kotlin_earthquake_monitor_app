package com.mgobeaalcoba.earthquakemonitor.api

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mgobeaalcoba.earthquakemonitor.database.getDatabase
import com.mgobeaalcoba.earthquakemonitor.main.MainRepository

class SyncWorkManager(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "SyncWorkManager"
    }

    private val database = getDatabase(appContext)
    private val repository = MainRepository(database)

    override suspend fun doWork(): Result {
        repository.fetchEarthquakes(true)

        return Result.success()
    }
}