package icu.nullptr.hidemyapplist.common.app_presets

import android.content.pm.ApplicationInfo
import java.util.zip.ZipFile

class RootAppsPreset() : BasePreset("root_apps") {
    override val exactPackageNames = setOf(
        // rooted apps
        "io.github.a13e300.ksuwebui",
        "io.github.muntashirakon.AppManager",
        "com.fox2code.mmm",
        "xzr.hkf",
        "com.speedsoftware.rootexplorer",
        "id.kuato.diskhealth",
        "com.sunilpaulmathew.debloater",
        "com.garyodernichts.downgrader",
        "eu.roggstar.getmitokens",
        "io.github.domi04151309.powerapp",
        "eu.roggstar.luigithehunter.batterycalibrate",
        "at.or.at.plugoffairplane",
        "tk.giesecke.phoenix",
        "com.corphish.nightlight.generic",
        "com.zinaro.cachecleanerwidget",
        "de.buttercookie.simbadroid",
        "simple.reboot.com",
        "ru.evgeniy.dpitunnel",
        "me.zhanghai.android.files",
        "ca.mudar.fairphone.peaceofmind",
        "com.gitlab.giwiniswut.rwremount",
        "com.machiav3lli.backup",
        "com.bartixxx.opflashcontrol",
        "dev.ukanth.ufirewall",
        "org.nuntius35.wrongpinshutdown",
        "ru.nsu.bobrofon.easysshfs",
        "x1125io.initdlight",
        "com.lonelycatgames.Xplore",
        "com.byyoung.setting",
        "web1n.stopapp",
        "ru.maximoff.apktool",
        "com.termux",
        "xzr.konabess",
        

        // kernel managers
        "flar2.exkernelmanager",
        "com.franco.kernel",
        "com.lybxlpsv.kernelmanager",
        "com.html6405.boefflakernelconfig",
        "ccc71.st.cpu",
    )

    override fun canBeAddedIntoPreset(appInfo: ApplicationInfo): Boolean {
        val packageName = appInfo.packageName

        if (containsPackage(packageName)) {
            return false
        }

        // FX File Manager
        if (packageName.startsWith("nextapp.fx")) {
            return true
        }

        // TotalCommander and its plugins
        if (packageName.startsWith("com.ghisler.")) {
            return true
        }

        // ZDevs apps (ZArchiver etc.)
        if (packageName.startsWith("ru.zdevs.")) {
            return true
        }

        // MiXplorer, MiXplorer Silver and its plugins
        if (packageName.startsWith("com.mixplorer")) {
            return true
        }

        // LSPosed and LSPatch
        if (packageName.startsWith("org.lsposed")) {
            return true
        }

        // Iconify
        if (packageName.startsWith("com.drdisagree.iconify")) {
            return true
        }

        // MMRL
        if (packageName.startsWith("com.dergoogler.mmrl")) {
            return true
        }

        // MT Manager
        if (packageName.startsWith("bin.mt.plus")) {
            return true
        }

        // Magisk
        if (packageName.endsWith(".magisk")) {
            return true
        }

        // APatch
        if (packageName.contains(".apatch.") || packageName.endsWith(".apatch")) {
            return true
        }

        // DataBackup
        if (packageName.startsWith("com.xayah.databackup")) {
            return true
        }

        // SmartPack Kernel Manager + Busybox Installer
        if (packageName.startsWith("com.smartpack.")) {
            return true
        }

        // F-Droid Privileged
        if (packageName.startsWith("org.fdroid.fdroid.privileged")) {
            return true
        }

        ZipFile(appInfo.sourceDir).use { zipFile ->
            if (findAppsFromLibs(zipFile) || findAppsFromAssets(zipFile)) {
                return true
            }
        }

        return false
    }

    private fun findAppsFromLibs(zipFile: ZipFile): Boolean {
        val entryNames = listOf(
            "libkernelsu.so",
            "libapd.so",
            "libmagisk.so",
            "libmmrl-kernelsu.so",
            "liblsplant.so",
        )
        val architectures = listOf("arm64-v8a", "armeabi-v7a")

        for (entry in entryNames) {
            for (arch in architectures) {
                if (zipFile.getEntry("lib/$arch/$entry") != null) {
                    return true
                }
            }
        }

        return false
    }

    private fun findAppsFromAssets(zipFile: ZipFile): Boolean {
        val entryNames = listOf(
            "gamma_profiles.json",
            "main.jar",
        )

        for (entry in entryNames) {
            if (zipFile.getEntry("assets/$entry") != null) {
                return true
            }
        }

        return false
    }
}
