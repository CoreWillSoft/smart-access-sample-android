import util.properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    compileSdk = AppCoordinates.Sdk.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AppCoordinates.Sdk.MIN_SDK_VERSION
        targetSdk = AppCoordinates.Sdk.TARGET_SDK_VERSION

        applicationId = AppCoordinates.APP_ID
        versionCode = AppCoordinates.APP_VERSION_CODE
        versionName = AppCoordinates.APP_VERSION_NAME
        testInstrumentationRunner = "io.template.app.TemplateAndroidRunner"

        resourceConfigurations += "en"
        resourceConfigurations += "de"
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions {
        animationsDisabled = true
        unitTests.apply {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
    sourceSets {
        named("test") {
            java.srcDirs("src/testUnit/kotlin", "src/testIntegration/kotlin")
        }
        named("androidTest") {
            java.srcDirs("src/testAndroid/kotlin")
        }
    }
    signingConfigs {
        listOf("debug", "release").forEach { configName ->
            util.SigningData.of(properties(file("config/signing/$configName/signing.properties")))
                ?.let {
                    val action = Action<com.android.build.api.dsl.ApkSigningConfig> {
                        storeFile = file(it.storeFile)
                        storePassword = it.storePassword
                        keyAlias = it.keyAlias
                        keyPassword = it.keyPassword
                    }
                    try {
                        getByName(configName, action::invoke)
                    } catch (e: Throwable) {
                        create(configName, action)
                    }
                }
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-dev"
            isDebuggable = true
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "config/proguard/kotlinx-serialization-proguard-rules.pro"
            )
            signingConfig = try {
                signingConfigs.getByName("release")
            } catch (e: Exception) {
                signingConfigs.getByName("debug")
            }
        }
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Deps.Presentation.Compose.COMPILER
    }
    lint {
        warningsAsErrors = true
        abortOnError = true
    }
}

dependencies {

    // Core
    coreLibraryDesugaring(Deps.Core.DESUGARING)
    implementation(Deps.Core.KOTLIN_REFLECT)
    implementation(Deps.Core.Coroutine.CORE)
    implementation(Deps.Core.Coroutine.ANDROID)
    implementation(Deps.Core.KOTLIN_RESULT)
    implementation(Deps.Core.Coroutine.PLAY_SERVICES)

    // DI
    implementation(Deps.Di.ANDROIDX)
    implementation(Deps.Di.ANDROIDX_NAV)
    implementation(Deps.Di.COMPOSE)

    // Presentation
    implementation(Deps.Presentation.Core.ANDROIDX_CORE_KTX)
    implementation(Deps.Presentation.Core.ANDROIDX_APPCOMPAT)
    implementation(Deps.Presentation.Core.ANDROID_MATERIAL)
    implementation(Deps.Presentation.Lifecycle.VIEWMODEL)
    implementation(Deps.Presentation.Lifecycle.COMMON)
    implementation(Deps.Presentation.Lifecycle.PROCESS)
    implementation(Deps.Presentation.Mvi.CORE)
    implementation(Deps.Presentation.Mvi.VIEWMODEL)
    implementation(Deps.Presentation.Mvi.COMPOSE)
    implementation(Deps.Presentation.Compose.ACTIVITY)
    implementation(Deps.Presentation.Compose.UI)
    implementation(Deps.Presentation.Compose.TOOLING_PREVIEW)
    implementation(Deps.Presentation.Compose.MATERIAL_3)
    implementation(Deps.Presentation.Compose.MATERIAL)
    implementation(Deps.Presentation.Compose.MATERIAL_ICONS_EXTENDED)
    implementation(Deps.Presentation.Compose.MAPS)
    implementation(Deps.Presentation.Compose.Accompanist.SYSTEM_UI_CONTROLLER)
    implementation(Deps.Presentation.Compose.Accompanist.PERMISSIONS)

    // Util
    implementation(Deps.Util.TIMBER)
    implementation(Deps.Util.NORDIC_SEMI_BLE)
    implementation(Deps.Util.NORDIC_SEMI_BLE_KTX)
    implementation(Deps.Presentation.PlayServices.MAPS)
    implementation(Deps.Presentation.PlayServices.LOCATION)

    // Unit Testing
    testImplementation(Deps.Testing.Kotest.RUNNER)
    testImplementation(Deps.Testing.Kotest.ASSERTIONS)
    testImplementation(Deps.Testing.Kotest.PROPERTY)
    testImplementation(Deps.Testing.Kotest.JUNIT_XML)
    testImplementation(Deps.Testing.Kotest.KOIN)
    testImplementation(Deps.Testing.Common.MOCKK_ANDROID)
    testImplementation(Deps.Testing.Common.MOCKK)
    testImplementation(Deps.Testing.Common.FIXTURE)
    // Presentation
    testImplementation(Deps.Presentation.Mvi.TEST)
    // Instrumentation
    testImplementation(Deps.Testing.Instrumentation.JUNIT_VINTAGE_ENGINE)
    testImplementation(Deps.Testing.Instrumentation.ROBOLECTRIC)
    testImplementation(Deps.Testing.Instrumentation.BOUNCY_CASTLE)
    testImplementation(Deps.Di.CORE_TEST)
    // UI Testing
    androidTestImplementation(Deps.Di.CORE_TEST)
    androidTestImplementation(Deps.Testing.Androidx.TEST_CORE)
    androidTestImplementation(Deps.Testing.Androidx.TEST_EXT_JUNIT)
    androidTestImplementation(Deps.Testing.Androidx.TEST_RULES)
    androidTestImplementation(Deps.Testing.Androidx.TEST_RUNNER)
    androidTestImplementation(Deps.Testing.Androidx.HAMCREST)
    androidTestImplementation(Deps.Testing.Androidx.ESPRESSO_CORE)
    androidTestImplementation(Deps.Testing.UI.KAKAO)
    androidTestImplementation(Deps.Testing.UI.BARISTA) { exclude(group = "org.jetbrains.kotlin") }
    debugImplementation(Deps.Testing.Androidx.TEST_MONITOR)
        ?.because("https://github.com/android/android-test/issues/731")
    androidTestImplementation(Deps.Presentation.Navigation.TESTING)
}
