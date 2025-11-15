package com.example.spottr

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Schedule the periodic sync worker
        setupPeriodicSync()
    }

    private fun setupPeriodicSync() {
        // Define constraints for the worker: it must have network connection.
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create a periodic work request that runs roughly every hour as a fallback.
        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // Enqueue the periodic work.
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "incident-fallback-sync", // Give it a unique name
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }
}
    