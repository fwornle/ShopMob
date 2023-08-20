    // Top-level build file where you can add configuration options common to all sub-projects/modules.

    // dependency versions
    extra.apply{
        set("gradle_version", "8.1.0")
    }

    allprojects {
        repositories {
            google()
            mavenCentral()
        }
    }

    plugins {
        id("com.android.application") version "8.1.0" apply false
        id("com.android.library") version "8.1.0" apply false
        id("org.jetbrains.kotlin.android") version "1.9.0" apply false
        id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
        id("androidx.navigation.safeargs.kotlin") version "2.7.0" apply false
        id("com.google.gms.google-services") version "4.3.15" apply false
        kotlin("jvm") version "1.9.0" apply false
        kotlin("plugin.serialization") version "1.9.0" apply false
    }

    repositories {
        google()
        mavenCentral()
    }

    tasks {
        register("clean", Delete::class) {
            delete(rootProject.buildDir)
        }
    }