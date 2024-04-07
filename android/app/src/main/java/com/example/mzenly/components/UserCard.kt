package com.example.mzenly.components

import android.content.res.Resources
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun getFormattedTimeDiff(nowDate: Date, userDate: Date): String {
    // made by gemini
    val diffInMs = nowDate.time - userDate.time

    val seconds = diffInMs / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    val unit: String
    val value: Long

    if (days > 0) {
        unit = "day"
        value = days
    } else if (hours > 0) {
        unit = "hour"
        value = hours
    } else if (minutes > 0) {
        unit = "minute"
        value = minutes
    } else {
        unit = "second"
        value = seconds
    }

    val formattedValue = when (value) {
        1L -> "$value $unit ago"
        else -> "$value ${unit}s ago"
    }

    return formattedValue
}

@Composable
fun UserCard(user: Map<String, String?>, content: @Composable() () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)
        .background(Color.White)
        .padding(15.dp, 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column () {
            Row (verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = user.getOrDefault("nickname", "").toString(),
                    fontSize = 28.sp
                )
                Text(
                    text = "•",
                    fontSize = 24.sp,
                    color = Color(0xFF9A9A9A)
                )
                val nowDate = Date()
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val userDate = formatter.parse(user.getOrDefault("last_update", Date().toString())!!)

                Text(
                    text = getFormattedTimeDiff(userDate!!, nowDate),
                    fontSize = 14.sp,
                    color = Color(0xFF9A9A9A)
                )
            }
            Text(
                text = user.getOrDefault("place", "").toString(),
                fontSize = 20.sp,
                textAlign = TextAlign.End
            )

        }

        Row (verticalAlignment = Alignment.CenterVertically){
            content()
        }
    }
}


@Preview
@Composable
private fun UserCardPreview(){
    MZenlyTheme {
        UserCard(user = mapOf(
            "nickname" to "Michael",
            "place" to "Insinoorinkatu, Tampere",
            "last_update" to "2024-04-07T13:54:53"
        )) {
            Text("Btns")
        }
    }

}