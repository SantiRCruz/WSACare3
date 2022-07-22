package com.example.wsacare.data.rest

import com.example.wsacare.core.Constants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class Rest {

    fun signIn(email:String,password:String):RequestBody{
        val json = JSONObject().apply {
            put("login",email)
            put("password",password)
        }
        return json.toString().toRequestBody("application/json".toMediaType())
    }

    fun get(endPoint: String):Request{
        return Request.Builder().url("${Constants.URL}/$endPoint").get().build()
    }

    fun post(endPoint:String,body: RequestBody):Request{
        return Request.Builder().url("${Constants.URL}/$endPoint").post(body).build()
    }

}