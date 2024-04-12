package com.example.mzenly

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@SuppressLint("MissingPermission")
class UserViewModel : ViewModel() {
    private val _userData = MutableStateFlow<ResponseState<ProfileData>>(ResponseState.Idle)
    private val _userLocation = MutableStateFlow(LatLng(0.0, 0.0))
    private var _token = MutableStateFlow<String?>(null);
    val userData = _userData.asStateFlow()
    val token = _token.asStateFlow()
    val userLocation = _userLocation.asStateFlow()

    fun getToken(context: Context): String? {
//        val editor: SharedPreferences.Editor = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).edit()
//        editor.putString("TOKEN", null)
//        editor.apply()
        _token.value = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE)
            .getString("TOKEN", null)

        return _token.value
    }

    fun setUserLocation(loc: LatLng){
        _userLocation.value = loc
    }

    fun createUser(nickname: String, context: Context){
        val call = mzenlyApi.createUser(nickname)
        val editor: SharedPreferences.Editor = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).edit()
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (!response.isSuccessful) return
                editor.putString("TOKEN", response.body()!!)
                _token.value = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).getString("TOKEN", "")!!
                editor.apply()
            }
            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(context, "Error: " + t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }



    fun updateUserData(user: UserUpdate, context: Context) {
        _token.value = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        val call = mzenlyApi.updateUser(_token.value!!, user)
        call.enqueue(EmptyCallback(context))
    }

    fun loadUserData(context: Context) {
        _token.value = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        val call = mzenlyApi.getUser(_token.value!!)
        call.enqueue(object : Callback<ProfileData> {
            override fun onResponse(call: Call<ProfileData>, response: Response<ProfileData>) {
                if (!response.isSuccessful){
                    Toast.makeText(context, "Error: " + response.raw().toString(), Toast.LENGTH_SHORT).show()
                    ResponseState.Error(Exception(response.raw().toString()))
                    return
                }
                _userData.value = ResponseState.Success(response.body()!!)
            }
            override fun onFailure(call: Call<ProfileData?>, t: Throwable) {
                Toast.makeText(context, "Error: " + t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
        _userData.value = ResponseState.Loading
    }

    fun acceptFriendRequest(ind: Int, context: Context){
        _token.value = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        val pd = (_userData.value as ResponseState.Success<ProfileData>).data
        val requestedUser = pd.requests[ind]
        val ud = pd.copy(
            friends = pd.friends.toMutableList().apply {
                add(requestedUser)
            },
            requests = pd.requests.toMutableList().apply {
                removeAt(ind)
            },
        )
        _userData.value = ResponseState.Success(ud)

        mzenlyApi.addFriend(_token.value!!, (requestedUser["id"] as Double).toInt()).enqueue(EmptyCallback(context))
    }

    fun rejectFriendRequest(ind: Int, context: Context){
        _token.value = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        val pd = (_userData.value as ResponseState.Success<ProfileData>).data
        mzenlyApi.rejectFriendRequest(_token.value!!, (pd.requests[ind]["id"] as Double).toInt()).enqueue(EmptyCallback(context))

        val ud = pd.copy(
            requests = pd.requests.toMutableList().apply {
                removeAt(ind)
            },
        )
        _userData.value = ResponseState.Success(ud)
    }

    fun deleteFriend(ind: Int, context: Context){
        _token.value = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        val pd = (_userData.value as ResponseState.Success<ProfileData>).data
        mzenlyApi.deleteFriend(_token.value!!, (pd.friends[ind]["id"] as Double).toInt()).enqueue(EmptyCallback(context))
        val ud = pd.copy(
            friends = pd.friends.toMutableList().apply {
                removeAt(ind)
            },
        )
        _userData.value = ResponseState.Success(ud)
    }

    fun sendFriendRequest(ind: Int, context: Context){
        _token.value = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).getString("TOKEN", "")!!

        val pd = (_userData.value as ResponseState.Success<ProfileData>).data
        mzenlyApi.sendFriendRequest(_token.value!!, (pd.near!![ind]["id"] as Double).toInt()).enqueue(EmptyCallback(context))
        val ud = pd.copy(
            near = pd.near.toMutableList().apply {
                removeAt(ind)
            },
        )
        _userData.value = ResponseState.Success(ud)
    }
}