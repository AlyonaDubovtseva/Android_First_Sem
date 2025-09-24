package com.example.first_homework

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.first_homework.ui.theme.First_HomeworkTheme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            First_HomeworkTheme {
                SecondScreen(
                    message = intent.getStringExtra("message") ?: ""
                )

            }
        }
    }
}

@Composable
fun SecondScreen(message: String) {
    val text = if (message.isEmpty()) "Второй экран" else message
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                val intent = Intent(context, ThirdActivity::class.java).apply {
                    putExtra("message", message)
                }
                context.startActivity(intent)
            }) {
                Text("Перейти на третий экран")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            }) {
                Text("перейти на первый экран")
            }
        }
    }
}

