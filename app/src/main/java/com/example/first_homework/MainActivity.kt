package com.example.first_homework

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.first_homework.ui.theme.First_HomeworkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            First_HomeworkTheme {
                FirstScreen()
            }
        }
    }
}

@Composable
fun FirstScreen() {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(text = "Первый экран",
                fontSize = 22.sp)
            Spacer(modifier = Modifier.height(32.dp))
            TextField(
                value = text,
                onValueChange = { text = it},
                label = { Text("Напишите что-то")},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val intent = Intent(context, SecondActivity::class.java).apply {
                    putExtra("message", text)
                }

                context.startActivity(intent)
            }) {
                Text("перейти на второй экран")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val intent = Intent(context, ThirdActivity::class.java).apply {
                    putExtra("message", text)
                }
                context.startActivity(intent)
            }) {
                Text("перейти на третий экран")
            }
        }
    }
}