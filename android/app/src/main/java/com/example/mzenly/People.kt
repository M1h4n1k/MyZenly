package com.example.mzenly

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mzenly.components.Header
import com.example.mzenly.components.UserCard
import com.example.mzenly.ui.theme.MZenlyTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask


@Composable
private fun PeopleBlock(title: String, users: MutableList<Map<String, String>>, onRemove: (Int) -> Unit, onAddFriend: (Int) -> Unit = {}){
    // Maybe add swiping logic in future
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        title,
        color = Color(0xFF686868),
        fontSize = 20.sp,
        modifier = Modifier.offset(15.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    for (i in users.indices) {
        UserCard(users[i]) {
            val fid = users[i]["id"]!!.toInt()
            when (title) {
                "Requests" -> {
                    Column (modifier = Modifier.padding(0.dp, 0.dp)) {
                        ActionButton(
                            Action("add", Color(0xFF4CCD99)),
                            onClick = {
                                mzenlyApi.addFriend(1, fid).enqueue(EmptyCallback())
                                onAddFriend(i)
                                onRemove(i)
                            }
                        )
                        ActionButton(
                            Action("remove", Color(0xFFFF204E)),
                            onClick = {
                                mzenlyApi.rejectFriendRequest(1, fid).enqueue(EmptyCallback())
                                onRemove(i)
                            }
                        )
                    }
                }
                "Friends" -> {
                    ActionButton(
                        Action("remove", Color(0xFFFF204E)),
                        onClick = {
                            mzenlyApi.deleteFriend(1, fid).enqueue(EmptyCallback())
                            onRemove(i)
                        }
                    )
                }
                "Near" -> {
                    ActionButton(
                        Action("request", Color(0xFFFFC700)),
                        onClick = {
                            mzenlyApi.sendFriendRequest(1, fid).enqueue(EmptyCallback())
                            onRemove(i)
                        }
                    )
                }
            }
        }
        if (i != users.size - 1) {
            Box (contentAlignment = Alignment.CenterEnd) {
                Divider(modifier = Modifier.fillMaxWidth(), color = Color.White)
                Divider(modifier = Modifier.fillMaxWidth(0.6f))
            }
        }
    }
}


private data class Action(val name: String, val color: Color)

@Composable
private fun ActionButton(action: Action, onClick: () -> Unit){
    Box (modifier = Modifier.clickable { onClick() }) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = action.name,
            modifier = Modifier.size(40.dp),
            tint = action.color
        )
        if (action.name == "remove") {
            Box(
                modifier = Modifier
                    .size(12.dp, 2.dp)
                    .offset(28.dp, 18.dp)
                    .background(action.color)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = action.name,
                modifier = Modifier
                    .size(20.dp)
                    .offset(25.dp, 6.dp),
                tint = action.color,
            )
        }
    }
}


@Composable
fun People(navController: NavHostController){
    var profileData by remember { mutableStateOf<ProfileData?>(null) }


    LaunchedEffect(Unit) {
        val call = mzenlyApi.getUser(1)
        call.enqueue(object : Callback<ProfileData?> {
            override fun onResponse(call: Call<ProfileData?>, response: Response<ProfileData?>) {
                if (!response.isSuccessful) return
                profileData = response.body() ?: return
            }
            override fun onFailure(call: Call<ProfileData?>, t: Throwable) { throw t }
        })
    }
//    profileData.value = ProfileData(
//        1, "HUY", "PIZDA", "0 0", Date(), false,
//        listOf(mapOf("nickname" to "123")), listOf(mapOf("nickname" to "123")), listOf(mapOf("nickname" to "123")))


    Column {
        Header(text = "People", navController = navController)
        if (profileData == null){
            Row (modifier = Modifier
                .fillMaxWidth()
                .offset(0.dp, 10.dp), horizontalArrangement = Arrangement.Center){
                CircularProgressIndicator()
            }
            return;
        }

        if (profileData!!.requests.isNotEmpty()) {
            PeopleBlock("Requests", profileData!!.requests,
                onRemove = { ind: Int ->
                    profileData = profileData!!.copy(requests = profileData!!.requests.toMutableList().apply {
                        removeAt(ind)
                    })
                },
                onAddFriend = { ind: Int ->
                    profileData = profileData!!.copy(friends = profileData!!.friends.toMutableList().apply {
                        add(profileData!!.requests[ind])
                    })
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (profileData!!.friends.isNotEmpty()) {
            PeopleBlock(
                "Friends", profileData!!.friends,
                onRemove = { ind: Int ->
                    profileData =
                        profileData!!.copy(friends = profileData!!.friends.toMutableList().apply {
                            removeAt(ind)
                        })
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (profileData!!.near.isNotEmpty()) {
            PeopleBlock("Near", profileData!!.near, onRemove = { ind: Int ->
                profileData = profileData!!.copy(near = profileData!!.near.toMutableList().apply {
                    removeAt(ind)
                })
            })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PeoplePreview(){
    val navController = rememberNavController()
    MZenlyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController, startDestination = "people") {
                composable("people") { People(navController) }
            }
        }
    }
}
