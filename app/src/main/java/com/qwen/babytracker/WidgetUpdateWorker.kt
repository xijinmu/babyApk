package com.qwen.babytracker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class WidgetUpdateWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        WidgetRepository.refresh(applicationContext)
        FeedingWidget.updateAll(applicationContext)
        return Result.success()
    }
}
