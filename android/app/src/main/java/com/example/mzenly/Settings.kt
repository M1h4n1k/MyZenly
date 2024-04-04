package com.example.mzenly

import android.os.Handler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mzenly.components.Header

import com.example.mzenly.ui.theme.MZenlyTheme
import com.example.mzenly.ui.theme.roundedSansFamily

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
private fun ButtonSwitch(text: String, active: Boolean, activeColor: Color, onClick: () -> Unit){
    val animatedColor by animateColorAsState(
        if (active) activeColor else Color(0xFF989898),
        label = "color"
    )

    Button(
        onClick = { onClick() }, // TODO add haptic feedback
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedColor ,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier.width(120.dp)
    ) {
        Text(text, fontSize = 30.sp, fontFamily = roundedSansFamily)
    }

}




@Composable
fun Settings(navController: NavHostController){
    var nickname by remember { mutableStateOf("") }
    var isVisibleToOthers by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    val handler by remember { mutableStateOf(Handler()) }
    var runnable by remember { mutableStateOf(Runnable {}) }

    fun updateInfo(){
        handler.removeCallbacks(runnable)
        runnable = Runnable {
            val ucall = mzenlyApi.updateUser(UserUpdate(1, nickname, null, null, isVisibleToOthers))
            ucall.enqueue(object : Callback<String?> {
                override fun onResponse(call: Call<String?>, response: Response<String?>) { }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    throw t
                }
            })
        }
        handler.postDelayed(runnable, 1000)
    }

    val call by remember { mutableStateOf(mzenlyApi.getUser(1)) }
    LaunchedEffect(Unit) {
        call.enqueue(object : Callback<ProfileData?> {
            override fun onResponse(call: Call<ProfileData?>, response: Response<ProfileData?>) {
                if (response.isSuccessful) {
                    val retrievedData = response.body() ?: return
                    nickname = retrievedData.nickname
                    isVisibleToOthers = retrievedData.visible
                    loading = false
                }
            }

            override fun onFailure(call: Call<ProfileData?>, t: Throwable) {
                throw t
            }
        })
    }


    Column {
        Header(text = "Settings", navController = navController)
        if (loading){
            Row (modifier = Modifier.fillMaxWidth().offset(0.dp, 10.dp), horizontalArrangement=Arrangement.Center){
                CircularProgressIndicator()
            }
            return;
        }
        Column (Modifier.padding(15.dp, 8.dp)) {
            Text(
                text = "Nickname",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
            )
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp)) {
                OutlinedTextField(  // TODO increase border width
                    modifier = Modifier.fillMaxWidth(),
                    value = nickname,
                    onValueChange = {
                        nickname = it
                        updateInfo()
                    },
                    label = { },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 36.sp, fontFamily = roundedSansFamily),
                    shape = RoundedCornerShape(15.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                )
            }
            
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Show to people near you?",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ButtonSwitch(
                    "No",
                    !isVisibleToOthers,
                    Color(0xFFFF204E),
                    onClick={
                        isVisibleToOthers = false
                        updateInfo()
                    }
                )
                ButtonSwitch(
                    "Yes",  // is it better to pass text in the curly parenthesis?
                    isVisibleToOthers,
                    Color(0xFF4CCD99),
                    onClick={
                        isVisibleToOthers = true
                        updateInfo()
                    }
                )
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SettingsPreview(){
    val navController = rememberNavController()
    MZenlyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController, startDestination = "settings") {
                composable("settings") { Settings(navController) }
            }
        }
    }
}