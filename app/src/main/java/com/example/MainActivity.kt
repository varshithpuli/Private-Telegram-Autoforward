package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.TeleSyncApp
import com.example.ui.theme.TeleSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeleSyncTheme {
                TeleSyncApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeleSyncPreview() {
    TeleSyncTheme {
        TeleSyncApp()
    }
}
