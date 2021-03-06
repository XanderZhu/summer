plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

androidExtensions {
    isExperimental = true
}

android {
    compileSdkVersion(targetVersion)

    defaultConfig {
        applicationId = "ru.napoleonit"
        minSdkVersion(minVersion)
        targetSdkVersion(targetVersion)
        multiDexEnabled = true
        resConfigs("en", "ru")
        vectorDrawables.useSupportLibrary = true
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            isDebuggable = true
            isTestCoverageEnabled = false
        }
    }

    packagingOptions {
        exclude("META-INF/proguard/androidx-annotations.pro")
        exclude("META-INF/*.version")
        exclude("META-INF/*.kotlin_module")
        exclude("**.kotlin_builtins")
        exclude("**.kotlin_metadata")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    implementation("org.kodein.di:kodein-di-erased-jvm:$kodeinVersion")
    implementation("com.russhwolf:multiplatform-settings:$multiplatformSettingVersion")

    implementation("com.google.android.material:material:1.0.0")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.recyclerview:recyclerview:1.0.0")

    implementation("androidx.multidex:multidex:2.0.1")

    implementation("androidx.core:core-ktx:1.1.0-beta01")

    implementation(project(":shared"))

    implementation("summer:summer:$summerVersion")
    implementation("summer:summer-androidx:$summerVersion")
//    implementation(project(":summer"))
//    implementation(project(":summer-androidx"))

    implementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}