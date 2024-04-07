package com.example.mzenly

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Date


var API_URL = "http://84.249.17.76:84/api/"

val gson: Gson = GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    .create()

class APIServiceInterceptor : Interceptor {

    var token : String = "a307764ea9b5e440c716ff494561801ba2b421c48552a8f2c7783557e5484b88";

    fun Token(token: String) {
        this.token = token;
    }
x
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        var request = chain.request()

        if(request.header("No-Authentication") == null){
            val finalToken = token
            request = request.newBuilder()
                .addHeader("Authorization", finalToken)
                .build()

        }
        return chain.proceed(request)
    }

}

val client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(APIServiceInterceptor())
    .build()

val rtf: Retrofit = Retrofit.Builder()
    .baseUrl(API_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

data class UserUpdate(
    val id: Int,
    val nickname: String? = null,
    val place: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val visible: Boolean? = null,
)

data class ProfileData (
    val id: Int,
    val nickname: String,
    val place: String,
    val latitude: Double,
    val longitude: Double,
    val last_update: Date,
    val visible: Boolean,

    val friends: MutableList<Map<String, String>>,
    val near: MutableList<Map<String, String>>?,
    val requests: MutableList<Map<String, String>>,
)


class EmptyCallback<T> : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        val bodyString = response.raw().toString()
        Log.e("Retrofit", "Response: $bodyString")
    }
    override fun onFailure(call: Call<T>, t: Throwable) { }
}

interface APIService {
    @GET("users/")
    fun getUser(): Call<ProfileData>

    @PUT("users/")
    fun updateUser(@Body user: UserUpdate): Call<String>

    @DELETE("friends/")
    fun deleteFriend(@Query("user_to_id") user2Id: Int): Call<String>

    @POST("friends/")
    fun addFriend(@Query("user_to_id") user2Id: Int): Call<String>

    @POST("friends/requests/")
    fun sendFriendRequest(@Query("user_to") userTo: Int): Call<String>


    @DELETE("friends/requests/")
    fun rejectFriendRequest(@Query("user_to") userTo: Int): Call<String>

}

val mzenlyApi: APIService = rtf.create(APIService::class.java)
