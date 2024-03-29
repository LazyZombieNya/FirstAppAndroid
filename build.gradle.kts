// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id ("com.android.application") version "8.2.2" apply false
    id ("com.android.library") version "8.2.2" apply false
    id ("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id ("org.jetbrains.kotlin.kapt") version "1.9.10" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id ("com.google.dagger.hilt.android") version "2.49" apply false
}


subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}