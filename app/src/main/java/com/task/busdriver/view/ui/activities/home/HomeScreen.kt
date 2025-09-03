package com.task.busdriver.view.ui.activities.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.myapplication3.view.styles.TextStyles
import com.task.busdriver.R
import com.task.busdriver.view.ui.activities.destination.DestinationsActivity

@SuppressLint("RememberInComposition")
@Composable
fun HomeScreen() {
    val colorGreen = Color(0xFF8FC9F8)
    val colorWhite = Color.White
    val gradient = Brush.linearGradient(0f to colorGreen, 1000f to colorWhite)
    val context = LocalContext.current

    val gifEnabledLoader = ImageLoader.Builder(context)
        .components {
            if ( SDK_INT >= 28 ) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    Box(
        modifier = Modifier.background(gradient)
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            Box(modifier = Modifier.height(height = 8.dp))
            AsyncImage(
                model = R.drawable.marker,
                imageLoader = gifEnabledLoader,
                contentDescription = null
            )

            Box(modifier = Modifier.height(20.dp))
            Button(
                shape = RoundedCornerShape(size = 8.dp),
                modifier =
                    Modifier
                        .width(300.dp)
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 16.dp),
                colors =
                    ButtonColors(
                        contentColor = Color.White,
                        containerColor = Color(0xff489BFC),
                        disabledContainerColor = Color(0xff489BFC),
                        disabledContentColor = Color(0xff489BFC),
                    ),
                onClick = {
                    context.startActivity(Intent(context, DestinationsActivity::class.java))
                },
                enabled = true,
                interactionSource = MutableInteractionSource(),){
                Icon(Icons.Outlined.LocationOn, contentDescription = "location")
                Text(
                    text = "Select your destination",
                    style = TextStyles().textStyleBold15,

                    textAlign = TextAlign.Center,

                )
            }
            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = "When you click you will be redirected to 2 map screens to set tracking route",
                style = TextStyles().textStyleNormal12.copy(color = Color.Black),

                textAlign = TextAlign.Center,

                )
        }

    }
}