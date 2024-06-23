plugins {
    id("com.android.application")

}

configurations {
    all {
        exclude(group = "org.jetbrains", module = "annotations")
    }
}

android {
    namespace = "com.example.androidchatrobot"
    compileSdk = 34

    defaultConfig {

        applicationId = "com.example.androidchatrobot"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}


dependencies {
    implementation ("io.noties.markwon:core:4.6.2")
    implementation ("io.noties.markwon:linkify:4.6.2")
    implementation ("io.noties.markwon:syntax-highlight:4.6.2")
    implementation ("io.noties.markwon:recycler:4.6.2")
    implementation ("io.noties.markwon:ext-latex:4.6.2")
    implementation ("ru.noties:jlatexmath-android:0.2.0")
    implementation ("com.github.linhao1998:Prism4jX:1.0.0")


    implementation ("androidx.appcompat:appcompat:1.2.0")
    implementation ("com.google.code.gson:gson:2.8.5")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
