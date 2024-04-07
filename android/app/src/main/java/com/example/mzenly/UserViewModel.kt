package com.example.mzenly

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserViewModel : ViewModel() {
    private val _userData = MutableStateFlow<ResponseState<ProfileData>>(ResponseState.Idle)
    private val _userLocation = MutableStateFlow<ResponseState<Location>>(ResponseState.Idle)
    val userData = _userData.asStateFlow()
    val userLocation = _userLocation.asStateFlow()

    fun setUserLocation(loc: Location){
        _userLocation.value = ResponseState.Success(loc)
    }

    fun loadUserData(context: Context) {
        val call = mzenlyApi.getUser()
        call.enqueue(object : Callback<ProfileData> {
            override fun onResponse(call: Call<ProfileData>, response: Response<ProfileData>) {
                if (!response.isSuccessful) return
                _userData.value = ResponseState.Success(response.body()!!)
            }
            override fun onFailure(call: Call<ProfileData?>, t: Throwable) { throw t }
        })
        _userData.value = ResponseState.Loading
    }

    fun acceptFriendRequest(ind: Int){
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

        mzenlyApi.addFriend(requestedUser["id"]!!.toInt()).enqueue(EmptyCallback())
    }

    fun rejectFriendRequest(ind: Int){
        val pd = (_userData.value as ResponseState.Success<ProfileData>).data
        mzenlyApi.rejectFriendRequest(pd.requests[ind]["id"]!!.toInt()).enqueue(EmptyCallback())

        val ud = pd.copy(
            requests = pd.requests.toMutableList().apply {
                removeAt(ind)
            },
        )
        _userData.value = ResponseState.Success(ud)
    }

    fun deleteFriend(ind: Int){
        val pd = (_userData.value as ResponseState.Success<ProfileData>).data
        mzenlyApi.deleteFriend(pd.friends[ind]["id"]!!.toInt()).enqueue(EmptyCallback())
        val ud = pd.copy(
            friends = pd.friends.toMutableList().apply {
                removeAt(ind)
            },
        )
        _userData.value = ResponseState.Success(ud)
    }

    fun sendFriendRequest(ind: Int){
        val pd = (_userData.value as ResponseState.Success<ProfileData>).data
        mzenlyApi.sendFriendRequest(pd.near!![ind]["id"]!!.toInt()).enqueue(EmptyCallback())
        val ud = pd.copy(
            near = pd.near.toMutableList().apply {
                removeAt(ind)
            },
        )
        _userData.value = ResponseState.Success(ud)
    }
}