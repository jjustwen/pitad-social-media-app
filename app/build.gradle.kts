plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.doanmobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.doanmobile"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-firestore:24.10.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")



    implementation ("de.hdodenhof:circleimageview:3.0.1")
    implementation ("androidx.core:core:1.0.0")
    implementation ("de.hdodenhof:circleimageview:3.0.1")

    // Import the BoM for the Firebase platform
    implementation (platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation ("com.google.firebase:firebase-database:16.0.4")
    implementation ("com.google.firebase:firebase-storage:16.0.4")
    implementation ("com.google.firebase:firebase-analytics")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("com.google.code.gson:gson:2.8.6")

//    implementation ("com.github.yalantis:ucrop:2.2.6")

//    implementation ("com.theartofdev.edmodo:android-image-cropper:2.8.0")



}