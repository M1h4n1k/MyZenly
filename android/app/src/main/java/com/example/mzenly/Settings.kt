package com.example.mzenly

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview


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
    var text by remember { mutableStateOf("Michael") }
    var isVisibleToOthers by remember { mutableStateOf(false) }

    Column {
        Header(text = "Settings", navController = navController)
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
                    value = text,
                    onValueChange = { text = it },
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
                    onClick={ isVisibleToOthers = false }
                )
                ButtonSwitch(
                    "Yes",  // is it better to pass text in the curly parenthesis?
                    isVisibleToOthers,
                    Color(0xFF4CCD99),
                    onClick={ isVisibleToOthers = true }
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