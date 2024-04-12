package com.example.mzenly.components

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.mzenly.R

@Composable
fun DeleteDialog (onDismiss: () -> Unit, context: Context) {
    AlertDialog(
        icon = {
            Icon(Icons.Filled.Delete, contentDescription = "Delete")
        },
        title = {
            Text(text = stringResource(R.string.reset_app))
        },
        text = {
            Text(text = stringResource(R.string.reset_explanation))
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val editor: SharedPreferences.Editor = context.getSharedPreferences("MZenlyPrefs", Context.MODE_PRIVATE).edit()
                    editor.putString("TOKEN", null)
                    editor.apply()
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.no))
            }
        }
    )
}