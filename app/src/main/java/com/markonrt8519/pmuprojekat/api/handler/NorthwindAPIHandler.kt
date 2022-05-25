package com.markonrt8519.pmuprojekat.api.handler

import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

class NorthwindAPIHandler {
    val CLIENT = OkHttpClient()
    val BASE_URL = "http://94.156.189.137/api/"

    fun getRequest(sUrl: String): String? {
        var result: String? = null
        try {
            val url = URL(BASE_URL + sUrl)
            val request = Request.Builder().url(url).build()
            val response = CLIENT.newCall(request).execute()
            result = response.body?.string()
        }
        catch (err: Error) {
            print("Error when executing get request: " + err.localizedMessage)
        }

        return result
    }
}