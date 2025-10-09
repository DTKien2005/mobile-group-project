package com.example.covid19app.offlinedata

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

object FileCache {
    private val client by lazy { OkHttpClient() }

    fun file(context: Context, name: String): File =
        File(context.filesDir, name)

    fun exists(context: Context, name: String): Boolean =
        file(context, name).exists()

    @Throws(IOException::class)
    fun downloadToFile(context: Context, url: String, name: String): File {
        val req = Request.Builder().url(url).build()
        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) throw IOException("HTTP ${resp.code}")
            val out = file(context, name)
            resp.body?.byteStream()?.use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            }
            return out
        }
    }

    fun readText(context: Context, name: String): String? =
        runCatching { file(context, name).readText() }.getOrNull()

    fun writeText(context: Context, name: String, text: String) {
        file(context, name).writeText(text)
    }
}
