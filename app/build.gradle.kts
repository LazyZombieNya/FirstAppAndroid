plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
}


android {
    namespace = "ru.netology.nmedia"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.netology.nmedia"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures.viewBinding = true
    buildFeatures.buildConfig = true

    buildTypes {
        release {
            isMinifyEnabled =  false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            manifestPlaceholders["usesCleartextTraffic"] = "false"
            buildConfigField("String","BASE_URL", "\"https://netomedia.ru\"")
        }
        debug {
            isMinifyEnabled =  false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            manifestPlaceholders["usesCleartextTraffic"] = "true"
            buildConfigField("String","BASE_URL", "\"http://10.0.2.2:9999\"")
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
dependencies {
    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")
    val room_version = "2.6.1"
    val retrofit_version = "2.9.0"
    val retrofitgson_version = "2.9.0"
    val okhttplogging_version = "4.12.0"
    val lifecycle_version = "2.2.0"
    val imagepicker_version = "2.1"
    val hilt_version = "2.49"
    val paging_version = "3.2.1"




    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation (platform("com.google.firebase:firebase-bom:32.4.1"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.android.gms:play-services-ads-base:22.4.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation ("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofitgson_version")
    implementation ("com.squareup.okhttp3:logging-interceptor:$okhttplogging_version")
    implementation ("com.sarveshathawale:kotlintoasts:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.3.9")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation ("com.github.dhaval2404:imagepicker:$imagepicker_version")
    implementation("com.google.dagger:hilt-android:$hilt_version")
    implementation("androidx.paging:paging-runtime-ktx:$paging_version")
    kapt("com.google.dagger:hilt-android-compiler:$hilt_version")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
kapt {
    correctErrorTypes = true
}