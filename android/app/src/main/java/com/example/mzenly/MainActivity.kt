package com.example.mzenly

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mzenly.components.Header
import com.example.mzenly.ui.theme.MZenlyTheme
import com.example.mzenly.ui.theme.roundedSansFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.LatLng


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MZenlyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun App(userViewModel: UserViewModel = viewModel()) {
    val navController = rememberNavController()
    val context = LocalContext.current
    userViewModel.getToken(context)
    val tokenRaw = userViewModel.token.collectAsState()
    Log.d("TOKEN", tokenRaw.value.toString())
    if (tokenRaw.value == null){
        RegistrationForm()
        return
    }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    if (!locationPermissionState.status.isGranted){
        val textToShow = if (locationPermissionState.status.shouldShowRationale) {
            stringResource(R.string.location_explanation1)
        } else {
            stringResource(R.string.location_explanation2)
        }
        Column{
            Header(text = stringResource(R.string.permissions), navController = null)
            Column (modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally){
                Text(textToShow, fontSize = 20.sp)
                Spacer(modifier = Modifier.weight(1.0f))
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text(
                        stringResource(R.string.give_permission),
                        fontSize = 22.sp,
                        modifier = Modifier.padding(20.dp, 7.dp),
                        fontFamily = roundedSansFamily
                    )
                }
            }
        }
        return
    }

    NavHost(navController, startDestination = "main") {
        composable(
            "main?lat={lat}&lng={lng}",
            arguments = listOf(
                navArgument("lat") { defaultValue = "" },
                navArgument("lng") { defaultValue = "" }
            )
        ) {backStackEntry ->
            var cameraPos: LatLng? = null
            if (
                backStackEntry.arguments?.getString("lat") != ""
                && backStackEntry.arguments?.getString("lng") != ""
            ){
                // It's worth adding checking for double, but since I am the only one working
                // on this project I'll try not to forget about it
                val lat = backStackEntry.arguments?.getString("lat")!!.toDouble()
                val lng = backStackEntry.arguments?.getString("lng")!!.toDouble()
                cameraPos = LatLng(lat, lng)
            }
            Main(navController, cameraPos)
        }
        composable("people") {  People(navController) }
        composable("settings") { Settings(navController) }
    }

}
