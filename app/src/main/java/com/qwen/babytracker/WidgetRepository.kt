package com.qwen.babytracker

import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object WidgetRepository {
    private const val PREFS = "feeding_widget"

    fun save(ctx: Context, feed: Int, diaper: Int, updated: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt("feed", feed).putInt("diaper", diaper).putString("updated", updated).apply()
    }

    fun feed(ctx: Context): Int = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("feed", 0)
    fun diaper(ctx: Context): Int = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt("diaper", 0)
    fun updated(ctx: Context): String = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("updated", "--:--") ?: "--:--"

    /** 拉取今日数据并缓存。返回是否成功。需在后台线程调用。 */
    fun refresh(ctx: Context): Boolean {
        return try {
            val conn = URL(Config.TODAY_API).openConnection() as HttpURLConnection
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            conn.requestMethod = "GET"
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val j = JSONObject(body)
            save(ctx, j.optInt("feedCount", 0), j.optInt("diaperCount", 0), formatTime(j.optString("updatedAt")))
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun formatTime(iso: String): String {
        return try {
            OffsetDateTime.parse(iso).atZoneSameInstant(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            "--:--"
        }
    }
}
