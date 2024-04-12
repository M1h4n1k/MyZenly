package com.example.mzenly

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mzenly.ui.theme.MZenlyTheme
import com.example.mzenly.ui.theme.roundedSansFamily
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
private fun MainNavButton(icon: ImageVector, text: String, onClick: () -> Unit){
    Button(
        modifier = Modifier
            .width(70.dp)
            .height(70.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp)
    ){
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(45.dp),
                tint = Color.White
            )
            Text(text, color = Color.White, fontSize = 12.sp)
        }
    }
}


@Composable
private fun PlaceHeader(place: String){
    Box (Modifier.offset(10.dp)) {
        Text(
            place,
            color = Color.White,
            fontSize = 45.sp,
        )

        Text(
            place,
            style = TextStyle.Default.copy(
                color = Color.Black,
                fontSize = 46.sp,
                fontFamily = roundedSansFamily,
                drawStyle = Stroke(
                    width = 6f,
                    join = StrokeJoin.Round
                )
            )
        )
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("MissingPermission")
@Composable
fun Main(
    navController: NavController,
    latLng: LatLng?,
    mapsViewModel: MapsViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
){
    val context = LocalContext.current
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val lastLocation = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
    if (lastLocation != null)
        userViewModel.setUserLocation(LatLng(lastLocation.latitude, lastLocation.longitude))
    val userLocation by userViewModel.userLocation.collectAsState()
    var cityHeader by remember { mutableStateOf("") }
    val cameraPositionState = rememberCameraPositionState {
        position = if (latLng != null)
            CameraPosition.fromLatLngZoom(latLng, 17f)
        else if (lastLocation != null)
            CameraPosition.fromLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), 17f)
        else
            CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 17f)

    }

    val address by mapsViewModel.addressDetail.collectAsState()

    LaunchedEffect (address) {
        if (address is ResponseState.Success){
            val addressSuccess = (address as ResponseState.Success<Address>).data
            if (addressSuccess != null){
                userViewModel.updateUserData(UserUpdate(
                    latitude=userLocation.latitude,
                    longitude=userLocation.longitude,
                    place=addressSuccess.thoroughfare + ", " + addressSuccess.locality
                ), context)
                cityHeader = addressSuccess.locality
            }
        }
    }

    val profileDataRaw by userViewModel.userData.collectAsState()
    LaunchedEffect(Unit){
        val locListener = android.location.LocationListener {
            p0 -> run {
                userViewModel.setUserLocation(LatLng(p0.latitude, p0.longitude))

                if (cameraPositionState.position.target == LatLng(0.0, 0.0))
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation, 17f)

                mapsViewModel.getMarkerAddressDetails(userLocation.latitude, userLocation.longitude,
                    context
                )
                Log.d("LOC UPD", "${p0.latitude} ${p0.longitude}")
            }
        }
        locationManager.removeUpdates(locListener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, locListener)

        mapsViewModel.getMarkerAddressDetails(userLocation.latitude, userLocation.longitude,
            context
        )

        userViewModel.loadUserData(context)
    }


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        /*
            I cannot make the map id work for some reason. It requires the latest
            version of maps sdk, however I guess I've already installed the latest one. However
            I still tried to upgrade, but I just couldn't make it work, so i just gave up,
            maybe I'll fix it later. It's not really necessary, but it provides a better style
            (less dense markers as well as removing names of streets
        */
//        googleMapOptionsFactory = { GoogleMapOptions().mapId("b68aea34d834a746") },
        properties = MapProperties(
            mapType = MapType.NORMAL,  // in emulator the roads are black if using normal type
            isBuildingEnabled = true,
            isIndoorEnabled = true,
            ),
        uiSettings = MapUiSettings()
    ) {
        MarkerComposable(state = MarkerState(position = userLocation)) {
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.you), fontFamily = roundedSansFamily, fontSize = 22.sp)
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = stringResource(R.string.your_position),
                    tint = Color.Red,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        if (profileDataRaw is ResponseState.Success<ProfileData>){
            val profileData by remember { mutableStateOf((profileDataRaw as ResponseState.Success<ProfileData>).data) }
            for (f in profileData.friends){  // For future development clustering would be nice
                MarkerComposable(
                    state = MarkerState(position = LatLng(f["latitude"] as Double, f["longitude"] as Double))
                ) {
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            f["nickname"] as String,
                            fontFamily = roundedSansFamily,
                            fontSize = 22.sp
                        )
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = "${f["nickname"]}'s position",
                            tint = Color.Blue,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }
        }
    }

    PlaceHeader(cityHeader)

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp, 120.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ){
        MainNavButton(Icons.Filled.Settings, stringResource(R.string.settings), onClick = { navController.navigate("settings") })
        Spacer(modifier = Modifier.height(10.dp))
        MainNavButton(Icons.Filled.Person, stringResource(R.string.people), onClick = { navController.navigate("people") })
    }
}


@Preview(showBackground = true)
@Composable
private fun MainPreview(){
    val navController = rememberNavController()
    MZenlyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController, startDestination = "main") {
                composable("main") { Main(navController, null) }
            }
        }
    }
}
