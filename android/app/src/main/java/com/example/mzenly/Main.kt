package com.example.mzenly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mzenly.ui.theme.MZenlyTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
            containerColor = Color(0xFF989898),
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
fun Main(navController: NavController){
    val tampere = LatLng(61.5, 23.76)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tampere, 17f)
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
            mapType = MapType.TERRAIN,
            isBuildingEnabled = true,
            isIndoorEnabled = true,
            ),
        uiSettings = MapUiSettings()
    ) {
        Marker(
            state = MarkerState(position = tampere),
            title = "You",
            snippet = "You"
        )
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp, 150.dp),
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