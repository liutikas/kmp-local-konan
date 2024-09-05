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
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        }
        desktopMain.dependencies {
        }
    }
}

val pathToKonan = "file:/usr/local/google/home/aurimas/Code/androidx-main/prebuilts/androidx/konan"

tasks.withType(KotlinNativeCompile::class.java).configureEach {
    compilerOptions.freeCompilerArgs.add(
        "-Xoverride-konan-properties=dependenciesUrl=$pathToKonan"
    )
}

tasks.withType(CInteropProcess::class.java).configureEach {
    settings.extraOpts +=
        listOf("-Xoverride-konan-properties", "dependenciesUrl=$pathToKonan")
}
