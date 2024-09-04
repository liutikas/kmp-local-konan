import org.jetbrains.kotlin.gradle.tasks.CInteropProcess
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    linuxX64()
    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        commonMain.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib")
        }
        commonTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test")
        }
        desktopMain.dependencies {
        }
    }
}

extensions.extraProperties["kotlin.native.distribution.baseDownloadUrl"] = "file:/usr/local/google/home/aurimas/Code/androidx-main/prebuilts/androidx/konan/nativeCompilerPrebuilts"

tasks.withType(KotlinNativeCompile::class.java).configureEach {
    compilerOptions.freeCompilerArgs.add(
        "-Xoverride-konan-properties=dependenciesUrl=file:/usr/local/google/home/aurimas/Code/androidx-main/prebuilts/androidx/konan"
    )
}

tasks.withType(CInteropProcess::class.java).configureEach {
    settings.extraOpts +=
        listOf("-Xoverride-konan-properties", "dependenciesUrl=file:/usr/local/google/home/aurimas/Code/androidx-main/prebuilts/androidx/konan")
}
