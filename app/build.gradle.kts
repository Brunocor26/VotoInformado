plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "pt.ubi.pdm.votoinformado"
    compileSdk = 34

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
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Credential Manager for Google Sign-In
    implementation("androidx.credentials:credentials:1.3.0-alpha01")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0-alpha01")

    //para json
    implementation("com.google.code.gson:gson:2.10.1")

    // Retrofit (para fazer os pedidos de rede)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Conversor Gson (para o Retrofit usar o Gson)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Biblioteca de Gráficos
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}