import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.denialshield"
    compileSdk = 34

    val keystorePropsFile = rootProject.file("keystore/keystore.properties")
    val keystoreProps = Properties()
    if (keystorePropsFile.exists()) {
        keystorePropsFile.inputStream().use { keystoreProps.load(it) }
    }

    fun resolveProp(name: String): String {
        return listOf(
            providers.gradleProperty(name).orNull,
            System.getenv(name),
            keystoreProps.getProperty(name)
        ).firstOrNull { !it.isNullOrBlank() }.orEmpty()
    }

    val releaseKeystorePath = resolveProp("KEYSTORE_PATH")
    val hasReleaseKeystore = releaseKeystorePath.isNotBlank()

    signingConfigs {
        create("release") {
            val keystorePassword = resolveProp("KEYSTORE_PASSWORD")
            val keyAlias = resolveProp("KEY_ALIAS")
            val keyPassword = resolveProp("KEY_PASSWORD")

            storeFile = if (releaseKeystorePath.isNotBlank()) file(releaseKeystorePath) else null
            storePassword = keystorePassword.ifBlank { null }
            this.keyAlias = keyAlias.ifBlank { null }
            this.keyPassword = keyPassword.ifBlank { null }
        }
    }

    defaultConfig {
        applicationId = "com.denialshield"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val hfToken = providers.gradleProperty("HF_TOKEN").orNull ?: ""
        buildConfigField("String", "HF_TOKEN", "\"$hfToken\"")
        
        ndk {
            abiFilters.addAll(listOf("x86_64", "armeabi-v7a", "arm64-v8a"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = if (hasReleaseKeystore) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // Room
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    
    // ML Kit Text Recognition (Bundled)
    implementation("com.google.mlkit:text-recognition:16.0.0-beta6")
    
    // PDF Handling
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Icons
    implementation("androidx.compose.material:material-icons-extended")

    // Local AI (MediaPipe GenAI)
    implementation("com.google.mediapipe:tasks-genai:0.10.11")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
