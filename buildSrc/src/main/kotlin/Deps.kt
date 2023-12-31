object BuildPluginsVersions {

    const val AGP = "7.4.1"
    const val KOTLIN = "1.9.0"

    const val DOKKA = "1.7.20"
    const val DETEKT = "1.19.0"

    object KTLINT {
        const val PLUGIN = "11.0.0"
        const val CONFIG = "0.40.0"
    }

    const val DEPENDENCY_UPDATES = "0.42.0"
    const val MAPS_SECRETS = "2.0.1"
}

object Deps {

    object Core {
        const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:${BuildPluginsVersions.KOTLIN}"
        const val KOTLIN_RESULT = "com.michael-bull.kotlin-result:kotlin-result:1.1.16"
        const val DESUGARING = "com.android.tools:desugar_jdk_libs:1.1.5"

        object Coroutine {
            private const val version = "1.6.4"
            const val CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
            const val ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
            const val PLAY_SERVICES = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$version"
        }
    }

    object Di {
        private const val koinVersion= "3.3.2"
        private const val koinAndroidVersion = "3.3.2"
        private const val koinComposeVersion = "3.4.1"
        const val CORE = "io.insert-koin:koin-core:$koinVersion"
        const val CORE_TEST = "io.insert-koin:koin-test:$koinVersion"
        const val ANDROIDX = "io.insert-koin:koin-android:$koinAndroidVersion"
        const val ANDROIDX_NAV = "io.insert-koin:koin-androidx-navigation:$koinAndroidVersion"
        const val COMPOSE = "io.insert-koin:koin-androidx-compose:$koinComposeVersion"
    }

    object Presentation {

        object Core {
            const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:1.8.0"
            const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:1.5.1"
            const val ANDROID_MATERIAL = "com.google.android.material:material:1.7.0"
        }

        object Lifecycle {
            private const val lifecycle_version = "2.5.1"
            const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
            const val COMMON = "androidx.lifecycle:lifecycle-common-java8:$lifecycle_version"
            const val PROCESS = "androidx.lifecycle:lifecycle-process:$lifecycle_version"

        }

        object Compose {
            private const val version = "1.4.3"
            const val COMPILER = "1.5.0"

            const val ACTIVITY = "androidx.activity:activity-compose:1.6.1"
            const val UI = "androidx.compose.ui:ui:$version"
            const val TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview:$version"
            const val MATERIAL_3 = "androidx.compose.material3:material3:1.1.0-alpha04"
            const val MATERIAL = "androidx.compose.material:material:1.3.1"
            const val MATERIAL_ICONS_EXTENDED = "androidx.compose.material:material-icons-extended:$version"
            const val MAPS = "com.google.maps.android:maps-compose:2.11.4"

            object Accompanist {
                private const val version = "0.31.0-alpha"
                const val SYSTEM_UI_CONTROLLER = "com.google.accompanist:accompanist-systemuicontroller:$version"
                const val PERMISSIONS = "com.google.accompanist:accompanist-permissions:$version"
            }

            object Testing {
                const val UI_JUNIT = "androidx.compose.ui:ui-test-junit4:$version"
                const val TOOLING = "androidx.compose.ui:ui-tooling:$version"
                const val MANIFEST = "androidx.compose.ui:ui-test-manifest:$version"
            }
        }

        object PlayServices {
            const val MAPS = "com.google.android.gms:play-services-maps:18.1.0"
            const val LOCATION = "com.google.android.gms:play-services-location:21.0.1"
        }

        object Navigation {
            const val VERSION = "2.5.3"
            const val FRAGMENT = "androidx.navigation:navigation-fragment-ktx:$VERSION"
            const val KTX = "androidx.navigation:navigation-ui-ktx:$VERSION"
            const val DYNAMIC_FEATURES =
                "androidx.navigation:navigation-dynamic-features-fragment:$VERSION"
            const val TESTING = "androidx.navigation:navigation-testing:$VERSION"
        }

        object Mvi {
            private const val version = "4.6.1"
            const val CORE = "org.orbit-mvi:orbit-core:$version"
            const val VIEWMODEL = "org.orbit-mvi:orbit-viewmodel:$version"
            const val COMPOSE = "org.orbit-mvi:orbit-compose:$version"

            const val TEST = "org.orbit-mvi:orbit-test:$version"
        }
    }

    object Util {
        const val TIMBER = "com.jakewharton.timber:timber:5.0.1"
        const val NORDIC_SEMI_BLE = "no.nordicsemi.android:ble:2.6.1"
        const val NORDIC_SEMI_BLE_KTX = "no.nordicsemi.android:ble-ktx:2.6.1"
    }

    object Testing {
        object Common {
            const val JUNIT = "junit:junit:4.13.2"
            private const val mockk_version = "1.13.2"
            const val MOCKK = "io.mockk:mockk:$mockk_version"
            const val MOCKK_ANDROID = "io.mockk:mockk-android:$mockk_version"
            const val FIXTURE = "com.appmattus.fixture:fixture:1.2.0"
        }

        object Kotest {
            private const val version = "5.5.4"
            const val RUNNER = "io.kotest:kotest-runner-junit5:$version"
            const val ASSERTIONS = "io.kotest:kotest-assertions-core:$version"
            const val PROPERTY = "io.kotest:kotest-property:$version"
            const val JUNIT_XML = "io.kotest:kotest-extensions-junitxml:$version"
            const val KOIN = "io.insert-koin:koin-test-junit4:3.2.0"
        }

        object Androidx {
            const val TEST_CORE = "androidx.arch.core:core-testing:2.1.0"
            const val TEST_RULES = "androidx.test:rules:1.4.0"
            const val TEST_RUNNER = "androidx.test:runner:1.4.0"
            const val TEST_EXT_JUNIT = "androidx.test.ext:junit-ktx:1.1.3"
            const val TEST_MONITOR = "androidx.test:core:1.4.0"
            const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.3.0"
            const val HAMCREST = "org.hamcrest:hamcrest:2.2"
        }

        object Instrumentation {
            const val JUNIT_VINTAGE_ENGINE = "org.junit.vintage:junit-vintage-engine:5.9.1"
            const val ROBOLECTRIC = "org.robolectric:robolectric:4.9"
            const val BOUNCY_CASTLE = "org.bouncycastle:bcprov-jdk15on:1.70"
        }

        object UI {
            const val KAKAO = "com.agoda.kakao:kakao:2.4.0"
            const val BARISTA = "com.adevinta.android:barista:4.2.0"

            object TestButler {
                private const val version = "2.2.1"
                const val LIBRARY = "com.linkedin.testbutler:test-butler-library:$version"
                const val APP = "com.linkedin.testbutler:test-butler-app:$version"
            }
        }
    }
}
