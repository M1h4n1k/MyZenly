package com.example.mzenly

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

val rtf: Retrofit = Retrofit.Builder()
    .baseUrl(API_URL)
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
    val near: MutableList<Map<String, String>>,
    val requests: MutableList<Map<String, String>>,
)

class ProfileViewModel : ViewModel() {
    private val _profileData = MutableLiveData<ProfileData>()
    val profileData: LiveData<ProfileData> = _profileData

    fun update(pd: ProfileData) {
        _profileData.value = pd
    }

    fun removeFriend(ind: Int){
        _profileData.value!!.friends.removeAt(ind)
    }


}

class EmptyCallback<T> : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        val bodyString = response.raw().toString()
        Log.e("Retrofit", "Response: $bodyString")
    }
    override fun onFailure(call: Call<T>, t: Throwable) { }
}

interface APIService {
    @GET("users/{user_id}")
    fun getUser(@Path("user_id") userId: Int): Call<ProfileData>

    @PUT("users/")
    fun updateUser(@Body user: UserUpdate): Call<String>

    @DELETE("friends/")
    fun deleteFriend(@Query("user1_id") user1Id: Int, @Query("user2_id") user2Id: Int): Call<String>

    @POST("friends/")
    fun addFriend(@Query("user1_id") user1Id: Int, @Query("user2_id") user2Id: Int): Call<String>

    @POST("friends/requests/")
    fun sendFriendRequest(@Query("user_from") userFrom: Int, @Query("user_to") userTo: Int): Call<String>


    @DELETE("friends/requests/")
    fun rejectFriendRequest(@Query("user_from") userFrom: Int, @Query("user_to") userTo: Int): Call<String>

}

val mzenlyApi: APIService = rtf.create(APIService::class.java)
