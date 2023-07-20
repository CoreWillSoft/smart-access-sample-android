package io.sample.smartaccess.app.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import io.sample.smartaccess.app.common.theme.SmartAccessTheme

class RootActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SmartAccessTheme { SmartAccessApp() } }
    }
}
