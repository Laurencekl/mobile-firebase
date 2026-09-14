plugins {
    id("com.android.application")
}

// Permite visualizar as telas enquanto a conexão do Firebase é configurada.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
} else {
    logger.warn("Firebase pendente: adicione app/google-services.json para habilitar a autenticação.")
}

android {
    namespace = "com.laurencekl.routrip"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.laurencekl.routrip"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.appcompat:appcompat:1.8.0")
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("com.google.android.material:material:1.14.0")
}
