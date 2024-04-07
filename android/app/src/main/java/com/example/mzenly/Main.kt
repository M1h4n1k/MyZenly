package com.example.mzenly

import android.annotation.SuppressLint
import android.location.Address
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
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
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


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("MissingPermission")
@Composable
fun Main(navController: NavController,
         mapsViewModel: MapsViewModel = viewModel(),
         userViewModel: UserViewModel = viewModel()){
    var cityHeader by remember { mutableStateOf("") }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation.value, 17f)
    }
    val context = LocalContext.current
    LaunchedEffect(userLocation.value){
        cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation.value, 17f)

        mapsViewModel.getMarkerAddressDetails(userLocation.value.latitude, userLocation.value.longitude,
            context
        )
    }

    val address by mapsViewModel.addressDetail.collectAsState()
    // not really cool way, but I don't understand neither how to update userModel from mainActivity
    // nor how to move the location getting logic outside of the mainActivity
    LaunchedEffect (address) {
        if (address is ResponseState.Success){
            val addressSuccess = (address as ResponseState.Success<Address?>).data
            if (addressSuccess != null){
                userViewModel.updateUserData(UserUpdate(
                    latitude=userLocation.value.latitude,
                    longitude=userLocation.value.longitude,
                    place=addressSuccess.thoroughfare + ", " + addressSuccess.locality
                ), context)
                cityHeader = addressSuccess.locality
            }
        }
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
            mapType = MapType.NORMAL,  // in emulator the roads are black if I use normal type
            isBuildingEnabled = true,
            isIndoorEnabled = true,
            ),
        uiSettings = MapUiSettings()
    ) {
        Marker(
            state = MarkerState(position = userLocation.value),
            title = "You",
            snippet = "You"
        )
    }

    PlaceHeader(cityHeader)

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp, 120.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ){
        MainNavButton(Icons.Filled.Settings, "Settings", onClick = { navController.navigate("settings") })
        Spacer(modifier = Modifier.height(10.dp))
        MainNavButton(Icons.Filled.Person, "People", onClick = { navController.navigate("people") })
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
                composable("main") { Main(navController) }
            }
        }
    }
}
