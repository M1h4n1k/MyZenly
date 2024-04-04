package com.example.mzenly

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.Date


var API_URL = "http://84.249.17.76:84/api/"

val gson: Gson = GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    .create()

val rtf: Retrofit = Retrofit.Builder()
    .baseUrl(API_URL)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()


data class UserUpdate(
    val id: Int,
    val nickname: String?,
    val place: String?,
    val coords: String?,
    val visible: Boolean?,
)

data class ProfileData (
    val id: Int,
    val nickname: String,
    val place: String,
    val coords: String,
    val last_update: Date,
    val visible: Boolean,

    val friends: List<Map<String, String>>,
    val near: List<Map<String, String>>,
    val requests: List<Map<String, String>>,
)

interface APIService {
    @GET("users/{user_id}")
    fun getUser(@Path("user_id") userId: Int): Call<ProfileData?>

    @PUT("users/")
    fun updateUser(@Body user: UserUpdate): Call<String>
}

val mzenlyApi: APIService = rtf.create(APIService::class.java)
