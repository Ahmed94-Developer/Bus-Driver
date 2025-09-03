package com.task.busdriver.view.ui.activities.splash

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.myapplication3.view.styles.TextStyles
import com.task.busdriver.R
import com.task.busdriver.view.ui.activities.auth.login.LoginActivity
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(){
    val context = LocalContext.current


    val colorGreen = Color(0xff8FC9F8)
    val colorWhite = Color.White
    val gradient = Brush.linearGradient(0f to colorGreen, 1000f to colorWhite)

    val gifEnabledLoader = ImageLoader.Builder(context)
        .components {
            if ( SDK_INT >= 28 ) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()


    LaunchedEffect(key1 = true) {
        delay(1800)
        context.startActivity(Intent(context,LoginActivity::class.java))
        (context as Activity).finish()
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(gradient),
        contentAlignment = Alignment.Center,
    ){
        Column(
           horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = R.drawable.map,
                imageLoader = gifEnabledLoader,
                contentDescription = null
            )
            Box(modifier = Modifier.height(height = 3.dp))
            Text(text = "Bus Driver", style = TextStyles().textStyleNormal18,
                textAlign = TextAlign.Center)
        }



    }

}