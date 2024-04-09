package com.example.mzenly

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mzenly.components.Header
import com.example.mzenly.components.MyTextField
import com.example.mzenly.ui.theme.MZenlyTheme
import com.example.mzenly.ui.theme.roundedSansFamily
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale


private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var locationCallback: LocationCallback
// I guess it's not the best way to do it, but I don't have enough experience with jetpack compose
// to do it properly
var userLocation = mutableStateOf(LatLng(0.0, 0.0))

class MainActivity : ComponentActivity() {

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            LocationRequest
                .Builder(LocationRequest.PRIORITY_HIGH_ACCURACY, 5000)
                .build(),
                locationCallback,
                Looper.getMainLooper()
        )
    }

    override fun onResume() {
        super.onResume()
        if (true) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hasLocationPermission = ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 1)
        }

        fusedLocationClient.lastLocation // get some old location until more precise one is loaded
            .addOnSuccessListener { location : Location? ->
                if (location != null)
                    userLocation.value = LatLng(location.latitude, location.longitude)
            }

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations){
                    if (location == null) continue
                    userLocation.value = LatLng(location.latitude, location.longitude)
                }
            }
        }


        setContent {
            MZenlyTheme {
                // A surface container using the 'background' color from the theme
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
    Log.d("TOKEB", tokenRaw.value.toString())
    if (tokenRaw.value == null){
        Log.d("TOKEB_IN", tokenRaw.value.toString())
        RegistrationForm()
        return
    }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    if (!locationPermissionState.status.isGranted){
        val textToShow = if (locationPermissionState.status.shouldShowRationale) {
            "Precise location is important for this app. Please grant the permission."
        } else {
            "Location permission required for this feature to be available. " +
                    "Please grant the permission"
        }
        Column{
            Header(text = "Permissions", navController = null)
            Column (modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally){
                Text(textToShow, fontSize = 20.sp)
                Spacer(modifier = Modifier.weight(1.0f))
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text("Give permission", fontSize = 22.sp, modifier = Modifier.padding(20.dp, 7.dp))
                }
            }
        }
        return
    }

    NavHost(navController, startDestination = "main") {
        composable("main") {  Main(navController) }
        composable("people") {  People(navController) }
        composable("settings") { Settings(navController) }
    }

}
