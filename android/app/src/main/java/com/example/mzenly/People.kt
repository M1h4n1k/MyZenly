package com.example.mzenly

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mzenly.components.Header
import com.example.mzenly.components.UserCard

import com.example.mzenly.ui.theme.MZenlyTheme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Composable
private fun PeopleBlock(title: String, users: List<Map<String, String>>){
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        title,
        color = Color(0xFF686868),
        fontSize = 20.sp,
        modifier = Modifier.offset(15.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    for (u in users){
        UserCard(u)
    }
}


@Composable
fun People(navController: NavHostController){

    val friends = listOf<Map<String, String>>(
        mapOf<String, String>("name" to "Michael", "place" to "Hervanta, DUO", "time" to "30 mins ago"),
        mapOf<String, String>("name" to "Alex", "place" to "TAMK", "time" to "2.5 hours ago"),
    )
    val requests = listOf<Map<String, String>>(
        mapOf<String, String>("name" to "Dinh", "place" to "Kaleva, Prisma", "time" to "1 hour ago"),
    )
    val near = listOf<Map<String, String>>(
        mapOf<String, String>("name" to "Dima", "place" to "Kebab House", "time" to "2 mins ago"),
    )


    Column {
        Header(text = "People", navController = navController)

        PeopleBlock("Requests", requests)
        Spacer(modifier = Modifier.height(10.dp))
        PeopleBlock("Friends", friends)
        Spacer(modifier = Modifier.height(10.dp))
        PeopleBlock("Near", near)
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
