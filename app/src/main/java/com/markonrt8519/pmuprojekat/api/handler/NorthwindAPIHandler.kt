package com.markonrt8519.pmuprojekat.api.handler

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

class NorthwindAPIHandler {
    val CLIENT = OkHttpClient()
    val BASE_URL = "http://94.156.189.137/api/"
    val MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()

    fun getRequest(sUrl: String): String? {
        var result: String? = null
        try {
            val url = URL(BASE_URL + sUrl)
            val request = Request.Builder().url(url).build()
            val response = CLIENT.newCall(request).execute()
            result = response.body?.string()
        }
        catch (err: Error) {
            print("Error when executing GET request: " + err.localizedMessage)
        }

        return result
    }

    fun postRequest(sUrl: String,data:String):String?{
        var result: String? = null
        try {
            // Create URL
            val url = URL(BASE_URL+sUrl)   // Build request
            val body=data.toRequestBody(MEDIA_TYPE)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()   // Execute request
            val response = CLIENT.newCall(request).execute()
            result = response.body?.string()
        }
        catch(err:Error) {
            print("Error when executing POST request: "+err.localizedMessage)
        }
        return result
    }

    fun putRequest(sUrl: String,data:String):String?{
        var result: String? = null
        try {
            // Create URL
            val url = URL(BASE_URL+sUrl)   // Build request
            val body=data.toRequestBody(MEDIA_TYPE)
            val request = Request.Builder()
                .url(url)
                .put(body)
                .build()   // Execute request
            val response = CLIENT.newCall(request).execute()
            result = response.body?.string()
        }
        catch(err:Error) {
            print("Error when executing PUT request: "+err.localizedMessage)
        }
        return result
    }

    fun deleteRequest(sUrl: String):String?{
        var result: String? = null
        try {
            // Create URL
            val url = URL(BASE_URL+sUrl)   // Build request
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()   // Execute request
            val response = CLIENT.newCall(request).execute()
            result = response.body?.string()
        }
        catch(err:Error) {
            print("Error when executing DELETE request: "+err.localizedMessage)
        }
        return result
    }
}