package com.example.mzenly.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mzenly.ui.theme.roundedSansFamily

@Composable
fun MyTextField(vv: String, onValueChange: (String) -> Unit){
    Row (modifier = Modifier
        .fillMaxWidth()) {
        OutlinedTextField(  // TODO increase border width
            modifier = Modifier.fillMaxWidth(),
            value = vv,
            onValueChange = {
                onValueChange(it)
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
}