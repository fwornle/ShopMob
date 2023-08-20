// Top-level build file where you can add configuration options common to all sub-projects/modules.
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {

    alias(build.plugins.android.application) apply false
    alias(build.plugins.android.library) apply false

    alias(build.plugins.kotlin) apply false
    alias(build.plugins.ksp) apply false

    alias(build.plugins.navigation.safeargs) apply false

    alias(build.plugins.gms) apply false

    alias(build.plugins.multiplatform) apply false
    alias(build.plugins.serialization) apply false

}

tasks {
    register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}