package com.example.mzenly

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query
import java.util.Date


var API_URL = "https://tuni.evicon.fun/api/"

val gson: Gson = GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    .create()

val rtf: Retrofit = Retrofit.Builder()
    .baseUrl(API_URL)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

data class UserUpdate(
    val nickname: String? = null,
    val place: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val visible: Boolean? = null,
)

data class ProfileData (
    val id: Int,
    val nickname: String,
    val place: String?,
    val latitude: Double?,
    val longitude: Double?,
    val last_update: Date,
    val visible: Boolean,

    val friends: MutableList<Map<String, Any>>,
    val near: MutableList<Map<String, Any>>?,
    val requests: MutableList<Map<String, Any>>,
)


class EmptyCallback<T> (private val context: Context) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        val bodyString = response.raw().toString()
        if (!response.isSuccessful){  // 200, 201
            Log.e("APICALL", "Error: $bodyString")
            Toast.makeText(context, "Error: $bodyString", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("APICALL", "Response: $bodyString")
    }
    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e("APICALL", "Internet error: " + t.localizedMessage)
        Toast.makeText(context, "Internet error: " + t.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}

interface APIService {
    @POST("users/")
    fun createUser(@Query("nickname") nickname: String): Call<String>

    @GET("users/")
    fun getUser(@Header("Authorization") authorization: String): Call<ProfileData>

    @PUT("users/")
    fun updateUser(@Header("Authorization") authorization: String, @Body user: UserUpdate): Call<String>

    @DELETE("friends/")
    fun deleteFriend(@Header("Authorization") authorization: String, @Query("user_to_id") user2Id: Int): Call<String>

    @POST("friends/")
    fun addFriend(@Header("Authorization") authorization: String, @Query("user_to_id") user2Id: Int): Call<String>

    @POST("friends/requests/")
    fun sendFriendRequest(@Header("Authorization") authorization: String, @Query("user_to") userTo: Int): Call<String>


    @DELETE("friends/requests/")
    fun rejectFriendRequest(@Header("Authorization") authorization: String, @Query("user_from") userFrom: Int): Call<String>

}

val mzenlyApi: APIService = rtf.create(APIService::class.java)
