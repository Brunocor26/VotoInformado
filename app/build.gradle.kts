
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "pt.ubi.pdm.votoinformado"
    compileSdk = 35

    defaultConfig {
        applicationId = "pt.ubi.pdm.votoinformado"
        minSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation("androidx.constraintlayout:constraintlayout:2.2.1") // Updated to latest stable version
    implementation(libs.picasso)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // WorkManager
    implementation("androidx.work:work-runtime:2.9.0")
    // Retrofit (para fazer os pedidos de rede)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Conversor Gson (para o Retrofit usar o Gson)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Biblioteca de Gráficos
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

    // Credential Manager (Keep for Google Sign-In via System)
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation(libs.googleid)

    // mapa
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    // Security Crypto
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
