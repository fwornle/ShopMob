plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")  // .kotlin --> KTX: nav.action accepts param. ("it")
    id("com.google.gms.google-services")
    id("kotlinx-serialization")
}

android {

    namespace = "com.tanfra.shopmob"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tanfra.shopmob"
        minSdk = 25
        targetSdk = 34
        versionCode = 1
        versionName = "1.2"
        resourceConfigurations += listOf("en")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // read API key from environment variable "SMOB_NET_API_KEY"
        // ... see: https://medium.com/@alexzaitsev/how-to-move-password-to-system-environment-variables-and-load-it-with-gradle-837bc18e6c8f
        val smobNetApiKey = System.getenv("SMOB_NET_API_KEY")
        buildConfigField("String", "SMOB_NET_API_KEY", "\"${smobNetApiKey}\"")

        // Facebook login: read SMOB_FB_CLIENT_TOKEN key from ENV variable
        // ... see: https://github.com/luggit/react-native-config/issues/21
        //          --> adapted from answer given by "prtkkmrsngh" (commented on 2 May 2017)
        manifestPlaceholders.putAll(mapOf("SMOB_FB_CLIENT_TOKEN" to System.getenv("SMOB_FB_CLIENT_TOKEN")))

        // network base URL
        // ... fake backend (emulator maps this to localhost@host)
        // ... production: BASE_URL = "https://shopmob.tanfra.com"
        buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:3000\"")
        //buildConfigField("String", "BASE_URL", "\"https://jp8rx9tjtg.execute-api.eu-central-1.amazonaws.com/dev/\"")
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    dataBinding {
        enable = true
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    testOptions.unitTests {
        isIncludeAndroidResources = true
        isReturnDefaultValues = true
    }

    // workaround to avoid build errors when running fragment-test dependent tests
    // https://www.codegrepper.com/code-examples/java/More+than+one+file+was+found+with+OS+independent+path+%27META-INF%2FAL2.0%27.+when+running+android+test
    packaging {
        jniLibs {
            excludes += listOf("META-INF/licenses/**")
        }
        resources {
            excludes += listOf("**/attach_hotspot_windows.dll", "META-INF/licenses/**", "META-INF/AL2.0", "META-INF/LGPL2.1")
        }
    }

    // enable compose
    buildFeatures {
        compose = true
        buildConfig = true
    }

    // kotlin/compose compatibility matrix: https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    val composeCompilerExtension = "1.5.2"
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerExtension
    }

}

dependencies {

    // kotlin
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    // timber - logging
    implementation(libs.timber)

    // tinder fsm
    implementation(libs.tinder.fsm)

    // orbit multiplatform (redux like ui state handling)
//    implementation(libs.orbit.core)  // KMM
    implementation(libs.orbit.viewmodel)  // android
    implementation(libs.orbit.compose)

    // androidx libraries
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.appcompat)
    implementation(libs.annotation)
    implementation(libs.legacy.support.v4)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.recyclerview)

    implementation(libs.bundles.androidx.navigation)
    implementation(libs.bundles.androidx.lifecycle)
//    kapt(libs.lifecycle.compiler)


    // compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.material)  // pullrefresh, ExperimentalMaterialApi (in BOM 2023.09)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.test.manifest)
//    implementation(libs.compose.material.icons.extended)
//    implementation(libs.compose.material3.window)
    implementation(libs.compose.navigation)
    implementation(libs.compose.activity)
    implementation(libs.compose.viewmodel)
    implementation(libs.compose.runtime)
    implementation(libs.compose.runtime.livedata)
//    implementation(libs.compose.runtime.rxjava2)
    implementation(libs.compose.state.events)  // 3rd party lib (!)


    // appearance
    implementation(libs.material)

    // depencency injection framework
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // serialization
    implementation(libs.kotlinx.serialization)

    // workManager
    implementation(libs.work.runtime.ktx)

    // ktor - HTTP client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content)
    implementation(libs.ktor.kotlinx.json)
    implementation(libs.http.logging)

    // room - mySQL DB
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // coil - loading an image from URL for display in ImageView
    implementation(libs.coil.compose)
    implementation(libs.coil)

    // firebase
    //
    // https://console.firebase.google.com/project/shopmob-335809/overview
    // https://firebase.google.com/docs/android/setup
    //
    // ... when using the BoM, you don"t specify versions in the associated library dependencies
    implementation(platform (libs.firebase.bom))
    implementation(libs.firebase.common.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.auth.ktx)

    // firebase-ui - simplify auth flows
    //
    // Stackoverflow on FireBase vs. FireBaseUI:
    // https://stackoverflow.com/questions/38799365/firebaseui-and-firebase-what-is-the-difference#:~:text=Firebase%20is%20a%20platform%20that,to%20user%20interface%20elements%20easier.
    //
    // Firebase is a platform that you use to build web and mobile applications. It consists of a
    // suite of cloud services and a set of SDKs (and in some cases REST APIs) to access those
    // services.
    //
    // FirebaseUI is a set of libraries that build on top of the Firebase SDKs to make binding to
    // user interface elements easier.
    //
    // Specifically: FirebaseUI for Android and for iOS wrap the Database and Authentication SDKs of
    // Firebase to make it easy to use the services in Android and iOS applications.
    //
    // FirebaseUI for web is a newer entrant to the field and only wraps the Authentication SDK.
    implementation(libs.firebase.ui.auth)

    // facebook
    //
    // Required only if Facebook login support is required
    // Find the latest Facebook SDK releases here: https://goo.gl/Ce5L94
    implementation(libs.facebook.login)

    // google - gms, play-services (maps & geofencing)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    // espresso - wrap idling resources
    // https://github.com/android/testing-samples/blob/main/ui/espresso/IdlingResourceSample/README.md
    implementation(libs.espresso.idling.resource)


    // ===================================================================================
    // ===================================================================================
    // ===================================================================================


    // local unit tests
    testImplementation(testLibs.junit)
    testImplementation(testLibs.hamcrest)
    testImplementation(testLibs.core.testing)
    testImplementation(testLibs.kotlinx.coroutines.android)
    testImplementation(testLibs.kotlinx.coroutines.test)
    testImplementation(testLibs.robolectric)
    testImplementation(testLibs.truth)
    testImplementation(testLibs.mockito.core)
    testImplementation(testLibs.koin.test)
    testImplementation(testLibs.koin.test.junit4)

    // androidX - JVM based testing
    testImplementation(testLibs.core.ktx)
    testImplementation(testLibs.junit.ktx)
    testImplementation(testLibs.rules)

    // Once https://issuetracker.google.com/127986458 is fixed this can be testImplementation
    debugImplementation(testLibs.fragment.testing)
//    implementation(testLibs.core)

    // ---------------------------------------------------------------------------------------

    // Test Orchestration
    androidTestImplementation(testLibs.runner)
    androidTestUtil(testLibs.orchestrator)
    androidTestImplementation(testLibs.espresso.intents)

    // uiAutomator Testing
    androidTestImplementation(testLibs.uiautomator)

    // AndroidX Test - Instrumented testing
    androidTestImplementation(testLibs.core.ktx)
    androidTestImplementation(testLibs.junit.ktx)
    androidTestImplementation(testLibs.kotlinx.coroutines.test)
    androidTestImplementation(testLibs.rules)
    androidTestImplementation(testLibs.room.testing)
    androidTestImplementation(testLibs.core.testing)
    androidTestImplementation(testLibs.robolectric.annotations)


    // composed based UI testing
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)


    // espresso
    androidTestImplementation(testLibs.espresso.core)
    androidTestImplementation(testLibs.espresso.contrib)
    androidTestImplementation(testLibs.espresso.intents)
    androidTestImplementation(testLibs.espresso.idling.concurrent)


    // junit
    androidTestImplementation(testLibs.junit)


    // koin
    androidTestImplementation(testLibs.koin.test) { exclude(group = "org.mockito") }
    androidTestImplementation(testLibs.koin.test.junit4)


    // mockito
    androidTestImplementation(testLibs.mockito.core)
    androidTestImplementation(testLibs.dexmaker.mockito)

}