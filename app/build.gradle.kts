import com.android.build.api.variant.impl.VariantOutputImpl
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Locale
import java.util.Properties
import kotlin.time.Duration.Companion.milliseconds

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kspPlugin)
}

val gitHash: String = providers.exec {
    commandLine("git", "rev-parse", "HEAD")
    isIgnoreExitValue = true
}.standardOutput.asText.map { it.trim().uppercase(Locale.getDefault()).substring(0,8) }.getOrElse("UNKNOWN")

android {
    namespace = "com.wmods.wppenhacer"
    //noinspection GradleDependency
    compileSdk = 37
    ndkVersion = "28.2.13676358"

    defaultConfig {
        applicationId = "com.owais.primewa"
        minSdk = 28
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 154
        versionName = "1.5.5 ($gitHash)"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        signingConfigs.create("config") {
            val localProps = Properties()
            val localPropsFile = rootProject.file("local.properties")
            if (localPropsFile.exists()) {
                localProps.load(localPropsFile.inputStream())
            }

            val keystorePath = System.getenv("KEYSTORE_FILE")
                ?: localProps.getProperty("RELEASE_STORE_FILE")
                ?: (project.findProperty("androidStoreFile") as String?)
                ?: "Owais.jks"
            val kStoreFile = rootProject.file(keystorePath)

            if (kStoreFile.exists()) {
                storeFile = kStoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                    ?: localProps.getProperty("RELEASE_STORE_PASSWORD")
                    ?: (project.findProperty("androidStorePassword") as String?)
                    ?: "Common@92"
                keyAlias = System.getenv("KEY_ALIAS")
                    ?: localProps.getProperty("RELEASE_KEY_ALIAS")
                    ?: (project.findProperty("androidKeyAlias") as String?)
                    ?: "owais"
                keyPassword = System.getenv("KEY_PASSWORD")
                    ?: localProps.getProperty("RELEASE_KEY_PASSWORD")
                    ?: (project.findProperty("androidKeyPassword") as String?)
                    ?: "Common@92"
            }
        }

        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }

        buildConfigField("Boolean", "RESET_ON_INSTALL", "false")

    }

    packaging {
        resources {
            excludes += "META-INF/**"
            excludes += "okhttp3/**"
            excludes += "kotlin/**"
            excludes += "org/**"
            excludes += "**.properties"
            excludes += "**.bin"
        }

        jniLibs {
            useLegacyPackaging = false
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildTypes {

        debug {
            isMinifyEnabled = project.hasProperty("minify") && project.findProperty("minify").toString().toBoolean()
            //noinspection NotShrinkingResources
            isShrinkResources = false
            signingConfig =
                if (signingConfigs["config"].storeFile != null) signingConfigs["config"] else signingConfigs["debug"]
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            isMinifyEnabled = true
            //noinspection NotShrinkingResources
            isShrinkResources = false
            signingConfig =
                if (signingConfigs["config"].storeFile != null) signingConfigs["config"] else signingConfigs["debug"]
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
        aidl = true
        resValues = true
    }


    lint {
        disable += "SelectedPhotoAccess"
        baseline = file("lint-baseline.xml")
    }

}

androidComponents {
    onVariants { variant ->
        variant.outputs.forEach { output ->
            (output as VariantOutputImpl).outputFileName.set("PrimeWA-1.5.5 ($gitHash).apk")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.colorpicker)
    implementation(files("libs/dexkit-android.aar"))
    implementation(libs.flatbuffers)
    compileOnly(libs.libxposed.legacy)
    ksp(libs.androidx.room.compiler)

    implementation(libs.core)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.room.runtime)
    implementation(libs.rikkax.appcompat)
    implementation(libs.rikkax.core)
    implementation(libs.material)
    implementation(libs.rikkax.material)
    implementation(libs.rikkax.material.preference)
    implementation(libs.rikkax.widget.borderview)
    implementation(libs.jstyleparser)
    implementation(libs.okhttp)
    implementation(libs.filepicker)
    implementation(libs.betterypermissionhelper)
    implementation(libs.bcpkix.jdk18on)
    implementation(libs.arscblamer)
    implementation(libs.markwon.core)
    implementation(libs.remote.preferences)
}


configurations.all {
    exclude("androidx.appcompat", "appcompat")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
}

tasks.configureEach {
    if (name.endsWith("ReleaseArtProfile")) {
        enabled = false
    }
}

interface InjectedExecOps {
    @get:Inject val execOps: ExecOperations
}


afterEvaluate {
    listOf("installWhatsappDebug", "installBusinessDebug").forEach { taskName ->
        tasks.findByName(taskName)?.doLast {
            runCatching {
                val injected  = project.objects.newInstance<InjectedExecOps>()
                runBlocking {
                    delay(500.milliseconds)
                    injected.execOps.exec {
                        commandLine(
                            "adb",
                            "shell",
                            "am",
                            "force-stop",
                            project.findProperty("debug_package_name")?.toString()
                        )
                    }
                    injected.execOps.exec {
                        commandLine(
                            "adb",
                            "shell",
                            "monkey",
                            "-p",
                            project.findProperty("debug_package_name")?.toString(),
                            "1"
                        )
                    }
                }
            }
        }
    }
}
