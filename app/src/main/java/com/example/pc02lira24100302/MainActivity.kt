package com.example.pc02lira24100302

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.pc02lira24100302.presentation.navigation.AppNavGraph
import com.example.pc02lira24100302.ui.theme.PC02LIRA24100302Theme


class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            PC02LIRA24100302Theme {
                AppNavGraph()
            }
        }
    }
}