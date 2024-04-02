package com.example.mzenly.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.mzenly.ui.theme.MZenlyTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun UserCard(user: Map<String, String>) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)
        .background(Color.White)
        .padding(15.dp, 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Column () {
            Text(
                text = user.getOrDefault("name", ""),
                fontSize = 30.sp
            )
        }

        Column (horizontalAlignment = Alignment.End) {
            Text(
                text = user.getOrDefault("place", ""),
                fontSize = 22.sp,
            )
            Text(
                text = user.getOrDefault("time", ""),
                fontSize = 16.sp,
                color = Color(0xFF9A9A9A)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun UserCardPreview(){
    MZenlyTheme {
        UserCard(user = mapOf(
            "name" to "Michael",
            "place" to "Hervanta, DUO",
            "time" to "30 mins ago"
        ))
    }

}