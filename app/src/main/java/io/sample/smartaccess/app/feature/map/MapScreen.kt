package io.sample.smartaccess.app.feature.map

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import io.sample.smartaccess.app.common.ui.PermissionBox

@Composable
internal fun MapScreen() {
    val permissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) addAll(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) else
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_ADVERTISE)
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_SCAN)
        } else addAll(
            setOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
            )
        )
    }

    PermissionBox(
        permissions = permissions,
        requiredPermissions = permissions,
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PermissionBox(
                permissions = listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            ) {
                GeofencingScreen()
            }
        } else {
            GeofencingScreen()
        }
    }
}