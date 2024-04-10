package com.example.mzenly.components

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mzenly.R
import java.text.SimpleDateFormat
import java.util.Date


fun getFormattedTimeDiff(nowDate: Date, userDate: Date, context: Context): String {
    // made by gemini
    val diffInMs = nowDate.time - userDate.time

    val seconds = diffInMs / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    val unit: String
    val value: Long

    if (days > 0) {
        unit = context.resources.getString(R.string.days)
        value = days
    } else if (hours > 0) {
        unit = context.resources.getString(R.string.hours)
        value = hours
    } else if (minutes > 0) {
        unit = context.resources.getString(R.string.minutes)
        value = minutes
    } else {
        unit = context.resources.getString(R.string.seconds)
        value = seconds
    }

    return "$value$unit ${context.resources.getString(R.string.ago)}"
}

@Composable
fun UserCard(user: Map<String, Any>, content: @Composable () -> Unit) {
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
                val userDate = formatter.parse(user.getOrDefault("last_update", Date()).toString())

                Text(
                    text = getFormattedTimeDiff(nowDate, userDate!!, LocalContext.current),
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