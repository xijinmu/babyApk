package com.qwen.babytracker

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews

class FeedingWidget : android.appwidget.AppWidgetProvider() {

    override fun onUpdate(context: Context, mgr: AppWidgetManager, ids: IntArray) {
        for (id in ids) render(context, mgr, id)
        // 每次刷新时后台拉一次最新数据
        Thread {
            WidgetRepository.refresh(context)
            updateAll(context)
        }.start()
    }

    override fun onEnabled(context: Context) {
        MainActivity.scheduleWidgetUpdates(context)
    }

    companion object {
        fun render(context: Context, mgr: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_feeding)
            views.setTextViewText(R.id.tv_feed, WidgetRepository.feed(context).toString())
            views.setTextViewText(R.id.tv_diaper, WidgetRepository.diaper(context).toString())
            views.setTextViewText(R.id.tv_updated, "更新 ${WidgetRepository.updated(context)}")
            mgr.updateAppWidget(id, views)
        }

        fun updateAll(context: Context) {
            val mgr = AppWidgetManager.getInstance(context)
            val ids = mgr.getAppWidgetIds(ComponentName(context, FeedingWidget::class.java))
            for (id in ids) render(context, mgr, id)
        }
    }
}
