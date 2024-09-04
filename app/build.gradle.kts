
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
