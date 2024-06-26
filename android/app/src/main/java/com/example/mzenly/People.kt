package com.example.mzenly

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mzenly.components.Header
import com.example.mzenly.components.UserCard
import com.example.mzenly.ui.theme.MZenlyTheme


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
private fun PeopleBlock(
    navController: NavHostController,
    title: String,
    users: MutableList<Map<String, Any>>,
    userViewModel: UserViewModel = viewModel(),
    mapsViewModel: MapsViewModel = viewModel()
    ){
    val context = LocalContext.current
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
        UserCard(users[i], onClick = {
            navController.navigate("main?lat=${users[i]["latitude"]}&lng=${users[i]["longitude"]}")
        }) {
            // I don't really like this nested thing here, but on the other hand
            // I can't come up with a solution of both having action buttons with changeable index,
            // cuz I want to keep the for-loop inside of this component
            when (title) {
                stringResource(R.string.requests) -> {
                    Column (modifier = Modifier.padding(0.dp, 0.dp)) {
                        ActionButton(
                            Action("add", Color(0xFF4CCD99)),
                            onClick = { userViewModel.acceptFriendRequest(i, context) }
                        )
                        ActionButton(
                            Action("remove", Color(0xFFFF204E)),
                            onClick = { userViewModel.rejectFriendRequest(i, context) }
                        )
                    }
                }
                stringResource(R.string.friends) -> {
                    ActionButton(
                        Action("remove", Color(0xFFFF204E)),
                        onClick = { userViewModel.deleteFriend(i, context) }
                    )
                }
                stringResource(R.string.near) -> {
                    ActionButton(
                        Action("request", Color(0xFFFFC700)),
                        onClick = { userViewModel.sendFriendRequest(i, context) }
                    )
                }
            }
        }
        if (i != users.size - 1) {
            Box (contentAlignment = Alignment.CenterEnd) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color.White)
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.6f))
            }
        }
    }
}


@Composable
fun People(navController: NavHostController, userViewModel: UserViewModel = viewModel()){
    val profileDataRaw by userViewModel.userData.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit){
        userViewModel.loadUserData(context)
    }
    if (profileDataRaw !is ResponseState.Success){
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            Header(text = stringResource(R.string.people), navController = navController)
            CircularProgressIndicator(modifier = Modifier.offset(0.dp, 10.dp))
        }
        return
    }
    val profileData = (profileDataRaw as ResponseState.Success<ProfileData>).data


    Column {
        Header(text = stringResource(R.string.people), navController = navController)

        if (profileData.requests.isNotEmpty()) {
            PeopleBlock(navController, stringResource(R.string.requests), profileData.requests)
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (profileData.friends.isNotEmpty()) {
            PeopleBlock(navController, stringResource(R.string.friends), profileData.friends)
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (profileData.near == null){
            Text(stringResource(R.string.near), color = Color(0xFF686868), fontSize = 20.sp, modifier = Modifier.offset(15.dp))
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(20.dp, 0.dp)
            ){
                Text(stringResource(R.string.near_explanation), fontSize = 20.sp)
                // maybe add button to settings or smth
            }
        } else if (profileData.near.isNotEmpty()) {
            PeopleBlock(navController, stringResource(R.string.near), profileData.near)
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
