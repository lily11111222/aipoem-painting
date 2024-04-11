plugins {
    id("com.android.application")
    id("com.chaquo.python")
}

android {
    namespace = "com.example.poemheavenjava"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.poemheavenjava"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        renderscriptTargetApi = 21
        renderscriptSupportModeEnabled = true

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
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
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
}

chaquopy {
    defaultConfig {
        
        buildPython("C:/Users/lily/AppData/Local/Programs/Python/Python311/python.exe")
        pip{
            // install "libgen-api"
            install("pillow")
            install("requests")
        }

    }
}


dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(files("libs\\Msc.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")
//    implementation("com.kongzue.dialogx:DialogX:0.0.48")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.kongzue.dialogx:DialogX:0.0.49")
}