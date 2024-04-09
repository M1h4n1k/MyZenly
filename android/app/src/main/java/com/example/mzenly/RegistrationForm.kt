package com.example.mzenly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mzenly.components.Header
import com.example.mzenly.components.MyTextField
import com.example.mzenly.ui.theme.roundedSansFamily


@Composable
fun RegistrationForm(userViewModel: UserViewModel = viewModel()){
    var nickname by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Header("Registration", null)
        Column(
            modifier = Modifier
                .padding(20.dp, 0.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nickname",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.offset((-10).dp)
            )
            MyTextField(nickname, onValueChange = {
                nickname = it
            })
        }
        Button(
            onClick = { userViewModel.createUser(nickname, context) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary ,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .offset(0.dp, (-20).dp)
                .padding(20.dp, 0.dp)
        ) {
            Text("Create account", fontSize = 22.sp, fontFamily = roundedSansFamily)
        }
    }
}