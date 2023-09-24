pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

include("", "app")
rootProject.name = "ShopMob"

// manage versions centrally
dependencyResolutionManagement {

    // keep separate version catalogues for "implementation", "test" and "androidTest"
    versionCatalogs {

        // version catalog for "plugins"
        create("build") {

            // general android
            version("android", "8.1.1")
            plugin("android-application", "com.android.application").versionRef("android")
            plugin("android-library", "com.android.library").versionRef("android")


            // kotlin, reflection-free serialization, ksp
            version("kotlin", "1.9.0")  // same as "implementation"
            version("ksp", "1.9.0-1.0.13")
            plugin("kotlin", "org.jetbrains.kotlin.android").versionRef("kotlin")
            plugin("multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")
            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            plugin("ksp", "com.google.devtools.ksp").versionRef("ksp")


            // androidX - navigation --> safeargs
            version("navigation", "2.7.0")
            plugin("navigation-safeargs", "androidx.navigation.safeargs.kotlin").versionRef("navigation")


            // google - maps, play services
            version("gms", "4.3.15")
            plugin("gms", "com.google.gms.google-services").versionRef("gms")

        }  // plugins


        // version catalog for "implementation" libs
        create("libs") {

            // kotlin
            version("kotlin", "1.9.0")
            library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin")
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            

            // timber - logging
            version("timber", "5.0.1")
            library("timber", "com.jakewharton.timber", "timber").versionRef("timber")


            // Tinder State Machine
            version("tinder-fsm", "0.3.0")
            library("tinder-fsm", "com.github.tinder", "StateMachine").versionRef("tinder-fsm")


            // Orbit Multiplatform - UI state handling (Redux style)
            version("orbit-mvi", "6.0.0")
//            library("orbit-core", "org.orbit-mvi", "orbit-core").versionRef("orbit-mvi")
            library("orbit-viewmodel", "org.orbit-mvi", "orbit-viewmodel").versionRef("orbit-mvi")
            library("orbit-compose", "org.orbit-mvi", "orbit-compose").versionRef("orbit-mvi")


            // androidx
            version("activity-ktx", "1.7.2")
            version("fragment-ktx", "1.6.1")
            version("appcompat", "1.6.1")
            version("annotation", "1.6.0")
            version("legacy-support-v4", "1.0.0")
            version("kotlinx-coroutines-android", "1.7.3")
            library("activity-ktx", "androidx.activity", "activity-ktx").versionRef("activity-ktx")
            library("fragment-ktx", "androidx.fragment", "fragment-ktx").versionRef("fragment-ktx")
            library("appcompat", "androidx.appcompat", "appcompat").versionRef("appcompat")
            library("annotation", "androidx.annotation", "annotation").versionRef("annotation")
            library(
                "legacy-support-v4",
                "androidx.legacy",
                "legacy-support-v4"
            ).versionRef("legacy-support-v4")
            library(
                "kotlinx-coroutines-android",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-android"
            ).versionRef("kotlinx-coroutines-android")


            // androidX - ui
            version("constraintlayout", "2.1.4")
            version("cardview", "1.0.0")
            version("recyclerview", "1.3.1")
            library("constraintlayout", "androidx.constraintlayout", "constraintlayout").versionRef(
                "constraintlayout"
            )
            library("cardview", "androidx.cardview", "cardview").versionRef("cardview")
            library(
                "recyclerview",
                "androidx.recyclerview",
                "recyclerview"
            ).versionRef("recyclerview")


            // androidX - navigation
            version("navigation", "2.6.0")  // 2.7.0 needs API 34
            library(
                "navigation-fragment-ktx",
                "androidx.navigation",
                "navigation-fragment-ktx"
            ).versionRef("navigation")
            library(
                "navigation-ui-ktx",
                "androidx.navigation",
                "navigation-ui-ktx"
            ).versionRef("navigation")
            bundle("androidx-navigation",
                listOf(
                    "navigation-fragment-ktx",
                    "navigation-ui-ktx",
                )
            )


            // androidX - lifecycle
            version("lifecycle", "2.6.1")
            version("lifecycle-extensions", "2.2.0")
            library(
                "lifecycle-compiler",
                "androidx.lifecycle",
                "lifecycle-compiler"
            ).versionRef("lifecycle") // kapt
            library(
                "lifecycle-common-java8",
                "androidx.lifecycle",
                "lifecycle-common-java8"
            ).versionRef("lifecycle")
            library(
                "lifecycle-viewmodel-ktx",
                "androidx.lifecycle",
                "lifecycle-viewmodel-ktx"
            ).versionRef("lifecycle")
            library(
                "lifecycle-livedata-ktx",
                "androidx.lifecycle",
                "lifecycle-livedata-ktx"
            ).versionRef("lifecycle")
            library(
                "lifecycle-extensions",
                "androidx.lifecycle",
                "lifecycle-extensions"
            ).versionRef("lifecycle-extensions")
            bundle("androidx-lifecycle",
                listOf(
                    "lifecycle-common-java8",
                    "lifecycle-viewmodel-ktx",
                    "lifecycle-livedata-ktx",
                    "lifecycle-extensions",
                )
            )


            // compose
            // ref: https://developer.android.com/jetpack/compose/setup#kotlin_1
            version("compose-bom", "2023.09.00")
            version("state-events", "2.0.3")
            library("compose-bom", "androidx.compose", "compose-bom").versionRef("compose-bom")
            library("compose-material", "androidx.compose.material", "material").withoutVersion()
            library("compose-material3", "androidx.compose.material3", "material3").withoutVersion()
            library("compose-material3-window", "androidx.compose.material3", "material3-window-size-class").withoutVersion()
            library("compose-material-icons-core", "androidx.compose.material", "material-icons-core").withoutVersion()
            library("compose-material-icons-extended", "androidx.compose.material", "material-icons-extended").withoutVersion()
            library("compose-ui-tooling-preview", "androidx.compose.ui", "ui-tooling-preview").withoutVersion()
            library("compose-ui-tooling", "androidx.compose.ui", "ui-tooling").withoutVersion()
            library("compose-ui-test-manifest", "androidx.compose.ui", "ui-test-manifest").withoutVersion()
            library("compose-ui-test-junit4", "androidx.compose.ui", "ui-test-junit4").withoutVersion()
            library("compose-activity", "androidx.activity", "activity-compose").versionRef("activity-ktx")  // integration (opt)
            library("compose-viewmodel", "androidx.lifecycle", "lifecycle-viewmodel-compose").versionRef("lifecycle")  // integration (opt)
            library("compose-runtime", "androidx.lifecycle", "lifecycle-runtime-compose").versionRef("lifecycle") // integration (opt)
            library("compose-runtime-livedata", "androidx.compose.runtime", "runtime-livedata").withoutVersion()  // integration (opt)
            library("compose-runtime-rxjava2", "androidx.compose.runtime", "runtime-rxjava2").withoutVersion()  // integration (opt)
            library("compose-navigation", "androidx.navigation", "navigation-compose").withoutVersion()
            library("compose-state-events", "com.github.leonard-palm", "compose-state-events").versionRef("state-events")  // 3rd party lib (!!)

            // appearance
            version("material", "1.9.0")
            library("material", "com.google.android.material", "material").versionRef("material")
            
            
            // depencency injection framework
            version("koin", "3.4.3")
            library("koin-core", "io.insert-koin", "koin-core").versionRef("koin")
            library("koin-android", "io.insert-koin", "koin-android").versionRef("koin")

            
            // serialization
            version("kotlinx-serialization", "1.6.0")
            library(
                "kotlinx-serialization",
                "org.jetbrains.kotlinx",
                "kotlinx-serialization-json"
            ).versionRef("kotlinx-serialization")

            
            // workmanager
            version("work-runtime-ktx", "2.8.1")
            library(
                "work-runtime-ktx",
                "androidx.work",
                "work-runtime-ktx"
            ).versionRef("work-runtime-ktx")

            
            // ktor - HTTP client
            version("ktor", "2.3.3")
            version("http-logging", "5.0.0-alpha.6")

            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef("ktor")
            library("ktor-client-okhttp", "io.ktor", "ktor-client-okhttp").versionRef("ktor")
            library("ktor-client-content", "io.ktor", "ktor-client-content-negotiation").versionRef(
                "ktor"
            )
            library("ktor-kotlinx-json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef("ktor")
            library(
                "http-logging",
                "com.squareup.okhttp3",
                "logging-interceptor"
            ).versionRef("http-logging")

            
            // room - mySQL DB
            version("room", "2.5.2")
            library("room-ktx", "androidx.room", "room-ktx").versionRef("room")
            library("room-runtime", "androidx.room", "room-runtime").versionRef("room")
            library("room-compiler", "androidx.room", "room-compiler").versionRef("room")  // ksp

            
            // coil - load images from URLs
            version("coil", "2.4.0")
            library("coil", "io.coil-kt", "coil").versionRef("coil")
            library("coil-compose", "io.coil-kt", "coil-compose").versionRef("coil")

            
            // firebase
            version("firebase-bom", "32.2.2")
            version("firebase-ui-auth", "8.0.2")
            library(
                "firebase-bom",
                "com.google.firebase",
                "firebase-bom"
            ).versionRef("firebase-bom")
            library(
                "firebase-common-ktx",
                "com.google.firebase",
                "firebase-common-ktx"
            ).withoutVersion()
            library(
                "firebase-storage-ktx",
                "com.google.firebase",
                "firebase-storage-ktx"
            ).withoutVersion()
            library(
                "firebase-messaging-ktx",
                "com.google.firebase",
                "firebase-messaging-ktx"
            ).withoutVersion()
            library(
                "firebase-analytics-ktx",
                "com.google.firebase",
                "firebase-analytics-ktx"
            ).withoutVersion()
            library(
                "firebase-auth-ktx",
                "com.google.firebase",
                "firebase-auth-ktx"
            ).withoutVersion()

            library(
                "firebase-ui-auth",
                "com.firebaseui",
                "firebase-ui-auth"
            ).versionRef("firebase-ui-auth")

            
            // facebook
            version("facebook-login", "16.1.3")
            library(
                "facebook-login",
                "com.facebook.android",
                "facebook-login"
            ).versionRef("facebook-login")

            
            // google - gms, play-services (maps & geofencing)
            version("play-services-location", "21.0.1")
            version("play-services-maps", "18.0.1")
            library(
                "play-services-location",
                "com.google.android.gms",
                "play-services-location"
            ).versionRef("play-services-location")
            library(
                "play-services-maps",
                "com.google.android.gms",
                "play-services-maps"
            ).versionRef("play-services-maps")


            // espresso
            version("espresso", "3.5.1")  // same as testImplementation
            library(
                "espresso-idling-resource",
                "androidx.test.espresso",
                "espresso-idling-resource"
            ).versionRef("espresso")

        }  // libs ("implementation")


        // ======================================================================================
        // ======================================================================================
        // ======================================================================================

        
        // Version catalog for "testImplementation" libs and "androidTestImplementation" libs
        create("testLibs") {

            // local unit tests
            version("junit", "4.13.2")
            version("core-testing", "2.2.0")
            version("kotlinx-coroutines", "1.7.3")

            library("junit", "junit", "junit").versionRef("junit")
            library(
                "core-testing",
                "androidx.arch.core",
                "core-testing"
            ).versionRef("core-testing")    // liveData testing
            library(
                "kotlinx-coroutines-android",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-android"
            ).versionRef("kotlinx-coroutines")
            library(
                "kotlinx-coroutines-test",
                "org.jetbrains.kotlinx",
                "kotlinx-coroutines-test"
            ).versionRef("kotlinx-coroutines")


            // hamcrest, truth, roboelectric
            version("hamcrest", "1.3")
            version("truth", "1.1.5")
            version("robolectric", "4.10.3")
            library("hamcrest", "org.hamcrest", "hamcrest-all").versionRef("hamcrest")
            library("truth", "com.google.truth", "truth").versionRef("truth")
            library("robolectric", "org.robolectric", "robolectric").versionRef("robolectric")
            library("robolectric-annotations", "org.robolectric", "annotations").versionRef("robolectric")

            
            // dependency injection
            version("koin", "3.4.3")  // same as implementation
            library("koin-test", "io.insert-koin", "koin-test").versionRef("koin")
            library("koin-test-junit4", "io.insert-koin", "koin-test-junit4").versionRef("koin")

            
            // mockito
            version("mockito", "5.4.0")
            version("dexmaker-mockito", "2.28.3")
            library("mockito-core", "org.mockito", "mockito-core").versionRef("mockito")
            library("dexmaker-mockito", "com.linkedin.dexmaker", "dexmaker-mockito").versionRef("dexmaker-mockito")

            
            // androidX test - JVM based /testing (no android dependency)
            version("test-core", "1.5.0")
            version("junit-ktx", "1.1.5")
            library("core-ktx", "androidx.test", "core-ktx").versionRef("test-core")
            library("rules", "androidx.test", "rules").versionRef("test-core")
            library("test-core", "androidx.test", "core").versionRef("test-core")
            library("junit-ktx", "androidx.test.ext", "junit-ktx").versionRef("junit-ktx")


            // (once https://issuetracker.google.com/127986458 is fixed this can be moved to 
            //  testImplementation)
            version("fragment-testing", "1.6.1")
            library(
                "fragment-testing",
                "androidx.fragment",
                "fragment-testing"
            ).versionRef("fragment-testing")


            // ------------------------------------------------------------------------------
            
            
            // androidTests
            version("runner", "1.5.2")
            version("orchestrator", "1.4.2")
            version("uiautomator", "2.2.0")
            library("runner", "androidx.test", "runner").versionRef("runner")
            library("orchestrator", "androidx.test", "orchestrator").versionRef("orchestrator")
            library("uiautomator", "androidx.test.uiautomator", "uiautomator").versionRef("uiautomator")


            // espresso
            version("espresso", "3.5.1")
            library("espresso-core", "androidx.test.espresso", "espresso-core").versionRef("espresso")
            library("espresso-contrib", "androidx.test.espresso", "espresso-contrib").versionRef("espresso")
            library("espresso-intents", "androidx.test.espresso", "espresso-intents").versionRef("espresso")
            library("espresso-idling-concurrent", "androidx.test.espresso.idling", "idling-concurrent").versionRef("espresso")


            // room
            version("room", "2.5.2")  // same as implementation
            library("room-testing", "androidx.room", "room-testing").versionRef("room")

        }  // "testImplementation"
        
    }

}