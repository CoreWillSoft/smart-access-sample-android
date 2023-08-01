package io.sample.smartaccess.app.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.sample.smartaccess.app.feature.map.MapScreen
import io.sample.smartaccess.app.feature.splash.SplashScreen

@Composable
internal fun SmartAccessApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable(route = "splash") { SplashScreen { navController.navigate("map") } }
        composable(route = "map") { MapScreen() }
    }
}