package com.example.mzenly

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mzenly.components.DeleteDialog
import com.example.mzenly.components.MyTextField

@Composable
private fun ButtonSwitch(text: String, active: Boolean, activeColor: Color, onClick: () -> Unit){
    val animatedColor by animateColorAsState(
        if (active) activeColor else Color(0xFF989898),
        label = "color"
    )
    Button(
        onClick = { onClick() },
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
fun Settings(navController: NavHostController, userViewModel: UserViewModel = viewModel()){
    val profileDataRaw by userViewModel.userData.collectAsState()
    var deleteToggle by remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(Unit){
        userViewModel.loadUserData(context)
    }

    if (deleteToggle) DeleteDialog(onDismiss = { deleteToggle = false }, context = context)
    Column {
        Header(text = stringResource(R.string.settings), navController = navController)
        if (profileDataRaw !is ResponseState.Success){
            Row (modifier = Modifier
                .fillMaxWidth()
                .offset(0.dp, 10.dp), horizontalArrangement=Arrangement.Center){
                CircularProgressIndicator()
            }
            Row {
                Button(
                    onClick = { deleteToggle = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF204E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(20.dp, 0.dp)
                ) {
                    Text(stringResource(R.string.reset_app), fontSize = 22.sp, fontFamily = roundedSansFamily)
                }
            }
            return;
        }
    }

    val handler by remember { mutableStateOf(Handler()) }
    var runnable by remember { mutableStateOf(Runnable {}) }
    var profileData by remember { mutableStateOf((profileDataRaw as ResponseState.Success<ProfileData>).data) }

    fun updateInfo(){
        handler.removeCallbacks(runnable)
        runnable = Runnable {
            userViewModel.updateUserData(
                UserUpdate(nickname=profileData.nickname, visible=profileData.visible),
                context
            )
        }
        handler.postDelayed(runnable, 1000)
    }

    Column {
        Header(text = stringResource(R.string.settings), navController = navController)
        Column (Modifier.padding(15.dp, 8.dp)) {
            Text(
                text = stringResource(R.string.nickname),
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
            )
            MyTextField(value = profileData.nickname, onValueChange = {
                profileData = profileData.copy(nickname = it)
                updateInfo()
            })
            
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = stringResource(R.string.show_to_people_near),
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
                    stringResource(R.string.no),
                    !profileData.visible,
                    Color(0xFFFF204E),
                    onClick={
                        profileData = profileData.copy(visible = false)
                        updateInfo()
                    }
                )
                ButtonSwitch(
                    stringResource(R.string.yes),  // is it better to pass text in the curly parenthesis?
                    profileData.visible,
                    Color(0xFF4CCD99),
                    onClick={
                        profileData = profileData.copy(visible = true)
                        updateInfo()
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row {
                Button(
                    onClick = { deleteToggle = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF204E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(20.dp, 0.dp)
                ) {
                    Text(stringResource(R.string.reset_app), fontSize = 22.sp, fontFamily = roundedSansFamily)
                }
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