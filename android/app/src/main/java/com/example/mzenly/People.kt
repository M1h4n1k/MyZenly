package com.example.mzenly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview


@Composable
private fun PeopleBlock(title: String, users: List<Map<String, String>>, actions: @Composable () -> Unit){
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
            actions()
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
private fun ActionButton(action: Action){
    Box {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = action.name,
            modifier = Modifier.size(40.dp),
            tint = action.color
        )
        if (action.name == "remove") {
            Box(modifier = Modifier
                .size(12.dp, 2.dp)
                .offset(28.dp, 18.dp)
                .background(action.color)
            )
        }
        else {
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

        // I intentionally pass actions as a parameter instead of the parenthesis
        PeopleBlock("Requests", requests, actions = {
            Column {
                ActionButton(Action("add", Color(0xFF4CCD99)))
                ActionButton(Action("remove", Color(0xFFFF204E)))
            }
        })
        Spacer(modifier = Modifier.height(10.dp))
        PeopleBlock("Friends", friends, actions = {
            ActionButton(Action("remove", Color(0xFFFF204E)))
        })
        Spacer(modifier = Modifier.height(10.dp))
        PeopleBlock("Near", near, actions = {
            ActionButton(Action("request", Color(0xFFFFC700)))
        })
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
