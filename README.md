# Repro for unexpected override-konan-properties=dependenciesUrl= behavior

## Desired behavior

For hermeticity reasons we'd like to have a build set up that does not use network at all and instead uses a local
set of files for Konan builds. We use `-Xoverride-konan-properties=dependenciesUrl=` to achieve this, however upgrade
from Kotlin Gradle Plugin 2.0.10 to 2.0.20 broke this workflow as KotlinNativeLink tasks now fetch from the network.

## Preparation

1. Create a directory that has the following
```
nativeCompilerPrebuilts/releases/2.0.20/linux-x86_64/kotlin-native-prebuilt-linux-x86_64-2.0.20.tar.gz
x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2.tar.gz
lldb-4-linux.tar.gz
llvm-11.1.0-linux-x64-essentials.tar.gz
libffi-3.2.1-2-linux-x86-64.tar.gz
```
which can be downloaded from
```
https://github.com/JetBrains/kotlin/releases/download/v2.0.20/kotlin-native-prebuilt-linux-x86_64-2.0.20.tar.gz
https://download.jetbrains.com/kotlin/native/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2.tar.gz
https://download.jetbrains.com/kotlin/native/lldb-4-linux.tar.gz
https://download.jetbrains.com/kotlin/native/llvm-11.1.0-linux-x64-essentials.tar.gz
https://download.jetbrains.com/kotlin/native/libffi-3.2.1-2-linux-x86-64.tar.gz
```
2. Update `gradle.properties` property `kotlin.native.distribution.baseDownloadUrl` to point to that directory
3. Update `app/build.gradle.kts` filed `val pathToKonan =` to point to that directory


## Reproduction

1. Do the preparation above
2. `./gradlew :app:linkDebugTestLinuxX64 --rerun`
3. `rm -fr ~/.konan`
4. `./gradlew :app:linkDebugTestLinuxX64 --rerun`

Observe you get the following log showing that archives are being fetched from the network
```
> Task :app:linkDebugTestLinuxX64
Downloading native dependencies (LLVM, sysroot etc). This is a one-time action performed only on the first run of the compiler.

(KonanProperties) Downloading dependency: https://download.jetbrains.com/kotlin/native/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2.tar.gz (0/101793914). 
(KonanProperties) Downloading dependency: https://download.jetbrains.com/kotlin/native/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2.tar.gz (101793914/101793914). Done.
Extracting dependency: /usr/local/google/home/aurimas/.konan/dependencies/cache/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2.tar.gz into /usr/local/google/home/aurimas/.konan/dependencies

(KonanProperties) Downloading dependency: https://download.jetbrains.com/kotlin/native/lldb-4-linux.tar.gz (0/52150087). 
(KonanProperties) Downloading dependency: https://download.jetbrains.com/kotlin/native/lldb-4-linux.tar.gz (52150087/52150087). Done.
Extracting dependency: /usr/local/google/home/aurimas/.konan/dependencies/cache/lldb-4-linux.tar.gz into /usr/local/google/home/aurimas/.konan/dependencies
...
```

## Observations

Successful runs print the following (note local file references)
```
> Task :app:linkDebugTestLinuxX64
Downloading native dependencies (LLVM, sysroot etc). This is a one-time action performed only on the first run of the compiler.

(KonanProperties) Downloading dependency: file:/usr/local/google/home/aurimas/Code/androidx-main/prebuilts/androidx/konan/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2.tar.gz (0/101793914). 
(KonanProperties) Downloading dependency: file:/usr/local/google/home/aurimas/Code/androidx-main/prebuilts/androidx/konan/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2.tar.gz (101793914/101793914). Done.
Extracting dependency: /usr/local/google/home/aurimas/.konan/dependencies/cache/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2.tar.gz into /usr/local/google/home/aurimas/.konan/dependencies

(KonanProperties) Downloading dependency: file:/usr/local/google/home/aurimas/Code/androidx-main/prebuilts/androidx/konan/lldb-4-linux.tar.gz (0/52150087). 
(KonanProperties) Downloading dependency: file:/usr/local/google/home/aurimas/Code/androidx-main/prebuilts/androidx/konan/lldb-4-linux.tar.gz (52150087/52150087). Done.
Extracting dependency: /usr/local/google/home/aurimas/.konan/dependencies/cache/lldb-4-linux.tar.gz into /usr/local/google/home/aurimas/.konan/dependencies
...
```

- Changing KGP back to 2.0.10 makes it use the local directory
- Using KGP 2.0.20 `implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")` from `app/build.gradle.kts` 
  makes it use local directory
- Instead of running `rm -fr ~/.konan`, running `rm -fr ~/.konan/dependencies` makes it use local directory
