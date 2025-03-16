plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.elainachat"
    compileSdk = 35

    packaging {
        resources {
            // 排除 Netty 相关的冲突文件
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/io.netty.versions.properties"

            // 排除之前 Spring 相关的配置
            excludes += "META-INF/spring*"
            excludes += "META-INF/spring.*"
            excludes += "META-INF/spring/**"
            excludes += "META-INF/*.kotlin_module"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    defaultConfig {
        applicationId = "com.example.elainachat"
        minSdk = 26
        targetSdk = 35
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
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    //添加Material Design库
    implementation("com.google.android.material:material:1.9.0")
    //emoji支持库
    implementation("androidx.emoji2:emoji2:1.3.0")
    implementation("androidx.emoji2:emoji2-bundled:1.3.0")
    implementation("io.netty:netty-all:4.1.92.Final")
    implementation("com.google.code.gson:gson:2.10.1")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("com.baomidou:mybatis-plus-core:3.5.5")
    // 分页功能
    implementation("com.baomidou:mybatis-plus-extension:3.5.5")
}