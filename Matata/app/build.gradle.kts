import java.util.regex.Pattern.compile

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}


android {
    namespace = "com.example.matata"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.matata"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {

    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")


    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.activity:activity:1.9.2")
    implementation(libs.play.services.maps)

    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.core)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("org.mockito:mockito-android:5.14.2")

    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.protobuf:protobuf-javalite:3.21.12")

//    implementation(files("C:/Users/chiro/AppData/Local/Android/Sdk/platforms/android-34/android.jar"))


    implementation("com.google.zxing:core:3.5.3")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    implementation ("androidx.camera:camera-camera2:1.1.0")
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    implementation ("androidx.camera:camera-view:1.1.0")

    implementation ("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation(libs.camera.view)
    implementation(libs.camera.lifecycle)
    implementation(libs.vision.common)
    implementation(libs.play.services.mlkit.barcode.scanning)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.google.firebase:firebase-storage:20.1.0")
    testImplementation("org.robolectric:robolectric:4.10.3")

    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    implementation ("androidx.palette:palette:1.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.android.gms:play-services-base:18.2.0")
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.8.1-rc")
    coreLibraryDesugaring("com.android.tools.desugar_jdk_libs:1.1.6")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    implementation("com.google.firebase:firebase-messaging")
}
