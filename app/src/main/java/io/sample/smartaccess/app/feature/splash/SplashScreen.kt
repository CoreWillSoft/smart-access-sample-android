package io.sample.smartaccess.app.feature.splash

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.sample.smartaccess.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashDispose: () -> Unit) {
    val window = (LocalContext.current as Activity).window
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val colorSystemBar = MaterialTheme.colorScheme.primary
    DisposableEffect(Unit) {
        scope.launch {
            systemUiController.setStatusBarColor(color = Color.Black)
            delay(2000)
            onSplashDispose()
        }
        onDispose {
            systemUiController.setStatusBarColor(color = colorSystemBar)
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.Center)) {
            Image(painter = painterResource(id = R.drawable.ic_splash), contentDescription = null)
            Spacer(modifier = Modifier.size(50.dp))
            Text(
                text = stringResource(R.string.smartaccess_splash_fuhr),
                fontSize = 30.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.smartaccess_splash_smartaccess),
                fontSize = 30.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Image(
            painter = painterResource(id = R.drawable.corewillsoft_logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .navigationBarsPadding()
        )
    }
}