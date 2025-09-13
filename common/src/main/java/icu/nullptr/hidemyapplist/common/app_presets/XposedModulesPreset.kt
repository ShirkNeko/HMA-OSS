package icu.nullptr.hidemyapplist.common.app_presets

import android.content.pm.ApplicationInfo
import java.util.zip.ZipFile

class XposedModulesPreset() : BasePreset("xposed") {
    override val exactPackageNames = setOf(
        // 米客
        "name.monwf.customiuizer",
        // 酒域——歌词获取
        "com.hchen.superlyric",
        // SuperLyric
        "cn.lyric.getter",
        // HyperOS Theme Cracking
        "com.fuck.HyperOSTheme",
        // R-安装组件-扩展
        "com.yxer.compo.module"
    )

    override fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean {
        if (containsPackage(appInfo.packageName)) {
            return true
        }

        ZipFile(appInfo.sourceDir).use { zipFile ->
            val manifestFile = zipFile.getInputStream(
                zipFile.getEntry("AndroidManifest.xml"))
            val manifestBytes = manifestFile.use { it.readBytes() }
            val manifestStr = String(manifestBytes, Charsets.US_ASCII)

            // Checking with binary because the Android system sucks
            if (manifestStr.contains("\u0000x\u0000p\u0000o\u0000s\u0000e\u0000d\u0000m\u0000o\u0000d\u0000u\u0000l\u0000e")) {
                return true
            } else if (zipFile.getEntry("META-INF/xposed")?.isDirectory ?: false) {
                return true
            }
        }

        return false
    }
}
