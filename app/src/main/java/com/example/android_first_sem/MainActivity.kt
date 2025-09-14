package com.example.android_first_sem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android_first_sem.ui.theme.Android_First_SemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                HelloCompose()
            }
        }
    }
}

@Preview
@Composable
fun HelloCompose(
    title: String = "Hello compose",
    content: String = "I am Alyona",
) {
   Box(modifier = Modifier
       .size(200.dp)) {
       Column {
           Text(text = title)
           Text(text = content)
           Image(painterResource(id = R.drawable.kotikmashetlapkoi),
               contentDescription = "cat_image",
               modifier = Modifier.padding(10.dp).size(250.dp))
       }
   }
}

