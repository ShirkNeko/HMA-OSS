import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.agp.app) apply false
    alias(libs.plugins.agp.lib) apply false
    alias(libs.plugins.nav.safeargs.kotlin) apply false
}

fun String.execute(currentWorkingDir: File = file("./")): String {
    val out = providers.exec {
        workingDir = currentWorkingDir
        commandLine = split("\\s".toRegex())
    }
    return out.standardOutput.asText.get().trim()
}

val localProperties = Properties()
localProperties.load(file("local.properties").inputStream())
val officialBuild by extra(localProperties.getProperty("officialBuild", "false") == "true")

fun getUncommittedSuffix(): String {
    if (officialBuild) return ""

    val result = "git status -s".execute()
    if (result.isEmpty()) {
        return ""
    }

    return "-canary_${result.count { it == '\n' } + 1}"
}

val gitHasUncommittedSuffix = getUncommittedSuffix()
val gitCommitCount = "git rev-list HEAD --count".execute().toInt()
val gitCommitHash = "git rev-parse --verify --short HEAD".execute()
val gitCommitCountAfterOss = gitCommitCount - 432

val minSdkVer by extra(29)
val targetSdkVer by extra(36)

val appVerCode = gitCommitCount + 0x6f7373
val appVerName by extra("zako-${gitCommitCountAfterOss}${gitHasUncommittedSuffix}")
val configVerCode by extra(93)
val serviceVerCode by extra(98)
val minBackupVerCode by extra(65)

val androidSourceCompatibility = JavaVersion.VERSION_21
val androidTargetCompatibility = JavaVersion.VERSION_21

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

fun Project.configureBaseExtension() {
    extensions.findByType<BaseExtension>()?.run {
        compileSdkVersion(targetSdkVer)

        defaultConfig {
            minSdk = minSdkVer
            targetSdk = targetSdkVer
            versionCode = appVerCode
            versionName = appVerName
            if (localProperties.getProperty("buildWithGitSuffix").toBoolean())
                versionNameSuffix = ".r${gitCommitCount}.${gitCommitHash}"

            consumerProguardFiles("proguard-rules.pro")
        }

        val config = localProperties.getProperty("fileDir")?.let {
            signingConfigs.create("config") {
                storeFile = file(it)
                storePassword = localProperties.getProperty("storePassword")
                keyAlias = localProperties.getProperty("keyAlias")
                keyPassword = localProperties.getProperty("keyPassword")
            }
        }

        buildTypes {
            all {
                signingConfig = config ?: signingConfigs["debug"]
            }
            named("release") {
                isMinifyEnabled = true
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }

        compileOptions {
            sourceCompatibility = androidSourceCompatibility
            targetCompatibility = androidTargetCompatibility
        }
    }

    extensions.findByType<ApplicationExtension>()?.run {
        buildTypes {
            named("release") {
                isShrinkResources = true
            }
        }
    }
}

subprojects {
    plugins.withId("com.android.application") {
        configureBaseExtension()
    }
    plugins.withId("com.android.library") {
        configureBaseExtension()
    }
}
