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
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Enables code shrinking and obfuscation
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false // Disable minification for debug builds
        }
    }
    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
    sourceSets {
        getByName("main") {
            java {
                srcDirs("src\\main\\java", "src\\main\\java\\com.example.matata",
                    "src\\main\\java",
                    "src\\main\\java\\com.example.matata\\TestClasses"
                )
            }
        }
    }

    configurations.all {
        resolutionStrategy {
            force("com.google.protobuf:protobuf-javalite:3.21.12")
            force("androidx.test:core:1.5.0")
            force("androidx.test.ext:junit:1.1.5")
            force("androidx.test.espresso:espresso-core:3.5.1")
        }
    }


}


dependencies {

    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.libraries.places:places:3.5.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")

    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation(libs.fragment.testing)
    testImplementation(libs.ext.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.activity:activity:1.9.2")
    implementation(libs.play.services.maps)

    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.core)

    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-idling-resource:3.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")


    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.firebase:firebase-auth")
    implementation ("jp.wasabeef:glide-transformations:4.3.0")

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
    implementation("com.google.protobuf:protobuf-javalite:3.21.12")
    androidTestImplementation("com.google.protobuf:protobuf-javalite:3.21.12")

    implementation ("androidx.palette:palette:1.0.0")
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    implementation("com.google.firebase:firebase-firestore:24.10.1") {
        exclude(group= "com.google.protobuf", module= "protobuf-lite")
    }
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
    implementation("com.google.firebase:firebase-functions")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Exclude protobuf-lite from test dependencies
    androidTestImplementation("androidx.test:core:1.5.0") {
        exclude(group= "com.google.protobuf", module= "protobuf-lite")
    }

    // Similarly, exclude from other test dependencies if needed
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") {
        exclude(group= "com.google.protobuf", module= "protobuf-lite")
    }

    // Explicitly include the correct version of protobuf-javalite for testing
    androidTestImplementation("com.google.protobuf:protobuf-javalite:3.21.2")



    // Ensure that Firestore is included in the test configuration
    androidTestImplementation("com.google.firebase:firebase-firestore:24.10.1") {
        exclude(group= "com.google.protobuf", module= "protobuf-lite")
    }
}