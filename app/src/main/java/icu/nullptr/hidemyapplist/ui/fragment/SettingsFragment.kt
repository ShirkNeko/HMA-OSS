package icu.nullptr.hidemyapplist.ui.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import icu.nullptr.hidemyapplist.common.CommonUtils
import icu.nullptr.hidemyapplist.common.Constants
import icu.nullptr.hidemyapplist.hmaApp
import icu.nullptr.hidemyapplist.service.ConfigManager
import icu.nullptr.hidemyapplist.service.PrefManager
import icu.nullptr.hidemyapplist.service.ServiceClient
import icu.nullptr.hidemyapplist.ui.activity.AboutActivity
import icu.nullptr.hidemyapplist.ui.util.makeToast
import icu.nullptr.hidemyapplist.ui.util.navController
import icu.nullptr.hidemyapplist.ui.util.setupToolbar
import icu.nullptr.hidemyapplist.util.ConfigUtils.Companion.getLocale
import icu.nullptr.hidemyapplist.util.LangList
import icu.nullptr.hidemyapplist.util.SuUtils
import org.frknkrc44.hma_oss.R
import org.frknkrc44.hma_oss.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : Fragment(R.layout.fragment_settings), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private val binding by viewBinding<FragmentSettingsBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.toolbar) {
            setupToolbar(
                toolbar = this,
                title = getString(R.string.title_settings),
                menuRes = R.menu.menu_about,
                onMenuOptionSelected = {
                    startActivity(Intent(requireContext(), AboutActivity::class.java))
                },
                navigationIcon = R.drawable.baseline_arrow_back_24,
                navigationOnClick = { navController.navigateUp() }
            )
            // isTitleCentered = true
        }

        if (childFragmentManager.findFragmentById(R.id.settings_container) == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.settings_container, SettingsPreferenceFragment())
                .commit()
        }

        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val barInsets = insets.getInsets(WindowInsets.Type.systemBars())
                binding.root.setPadding(
                    barInsets.left,
                    barInsets.top,
                    barInsets.right,
                    barInsets.bottom,
                )
            } else {
                @Suppress("deprecation")
                binding.root.setPadding(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom,
                )
            }

            insets
        }
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        val fragment = childFragmentManager.fragmentFactory.instantiate(requireContext().classLoader, pref.fragment!!)
        fragment.arguments = pref.extras
        childFragmentManager.beginTransaction()
            .replace(R.id.settings_container, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }

    class SettingsPreferenceDataStore : PreferenceDataStore() {
        override fun getBoolean(key: String, defValue: Boolean): Boolean {
            return when (key) {
                "followSystemAccent" -> PrefManager.followSystemAccent
                "blackDarkTheme" -> PrefManager.blackDarkTheme
                "detailLog" -> ConfigManager.detailLog
                "hideIcon" -> PrefManager.hideIcon
                "bypassRiskyPackageWarning" -> PrefManager.bypassRiskyPackageWarning
                "appDataIsolation" -> CommonUtils.isAppDataIsolationEnabled
                "voldAppDataIsolation" -> CommonUtils.isVoldAppDataIsolationEnabled
                "disableActivityLaunchProtection" -> ConfigManager.disableActivityLaunchProtection
                "forceMountData" -> ConfigManager.forceMountData
                else -> throw IllegalArgumentException("Invalid key: $key")
            }
        }

        override fun getString(key: String, defValue: String?): String {
            return when (key) {
                "language" -> PrefManager.locale
                "themeColor" -> PrefManager.themeColor
                "darkTheme" -> PrefManager.darkTheme.toString()
                "maxLogSize" -> ConfigManager.maxLogSize.toString()
                else -> throw IllegalArgumentException("Invalid key: $key")
            }
        }

        override fun putBoolean(key: String, value: Boolean) {
            when (key) {
                "followSystemAccent" -> PrefManager.followSystemAccent = value
                "blackDarkTheme" -> PrefManager.blackDarkTheme = value
                "detailLog" -> ConfigManager.detailLog = value
                "forceMountData" -> ConfigManager.forceMountData = value
                "hideIcon" -> PrefManager.hideIcon = value
                "bypassRiskyPackageWarning" -> PrefManager.bypassRiskyPackageWarning = value
                "disableActivityLaunchProtection" -> ConfigManager.disableActivityLaunchProtection = value
                "appDataIsolation" -> Unit
                "voldAppDataIsolation" -> Unit
                else -> throw IllegalArgumentException("Invalid key: $key")
            }
        }

        override fun putString(key: String, value: String?) {
            when (key) {
                "language" -> PrefManager.locale = value!!
                "themeColor" -> PrefManager.themeColor = value!!
                "darkTheme" -> PrefManager.darkTheme = value!!.toInt()
                "maxLogSize" -> ConfigManager.maxLogSize = value!!.toInt()
                else -> throw IllegalArgumentException("Invalid key: $key")
            }
        }
    }

    class DataIsolationPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = SettingsPreferenceDataStore()
            setPreferencesFromResource(R.xml.settings_data_isolation, rootKey)

            findPreference<SwitchPreferenceCompat>("appDataIsolation")?.let {
                it.setOnPreferenceChangeListener { _, newValue ->
                    handleIsolationChange(
                        preference = it,
                        enabled = newValue as Boolean,
                        property = Constants.ANDROID_APP_DATA_ISOLATION_ENABLED_PROPERTY,
                        checker = CommonUtils::isAppDataIsolationEnabled
                    )
                    false
                }
            }

            findPreference<SwitchPreferenceCompat>("voldAppDataIsolation")?.let {
                it.setOnPreferenceChangeListener { _, newValue ->
                    val enabled = newValue as Boolean
                    if (enabled) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.settings_warning)
                            .setMessage(R.string.settings_vold_warning)
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                handleIsolationChange(
                                    preference = it,
                                    enabled = true,
                                    property = Constants.ANDROID_VOLD_APP_DATA_ISOLATION_ENABLED_PROPERTY,
                                    checker = CommonUtils::isVoldAppDataIsolationEnabled
                                )
                            }
                            .setNegativeButton(android.R.string.cancel) { _, _ ->
                                it.isChecked = false
                            }
                            .setCancelable(false)
                            .show()
                    } else {
                        handleIsolationChange(
                            preference = it,
                            enabled = false,
                            property = Constants.ANDROID_VOLD_APP_DATA_ISOLATION_ENABLED_PROPERTY,
                            checker = CommonUtils::isVoldAppDataIsolationEnabled
                        )
                    }
                    false
                }
            }
        }

        private fun handleIsolationChange(preference: SwitchPreferenceCompat, enabled: Boolean, property: String, checker: () -> Boolean) {
            val value = if (enabled) 1 else 0
            val result = SuUtils.execPrivileged("setprop $property $value")
            if (result) makeToast(R.string.settings_need_reboot)
            else makeToast(R.string.settings_permission_denied)
            preference.isChecked = checker()
        }
    }

    class SettingsPreferenceFragment : PreferenceFragmentCompat() {
        private fun Boolean.enabledString(): String {
            return if (this) getString(R.string.enabled)
            else getString(R.string.disabled)
        }

        private fun configureDataIsolation() {
            findPreference<Preference>("dataIsolation")?.let {
                it.isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                it.summary = when {
                    it.isEnabled -> getString(
                        R.string.settings_data_isolation_summary,
                        CommonUtils.isAppDataIsolationEnabled.enabledString(),
                        CommonUtils.isVoldAppDataIsolationEnabled.enabledString(),
                        ConfigManager.forceMountData.enabledString()
                    )
                    else -> getString(R.string.settings_data_isolation_unsupported)
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            preferenceManager.preferenceDataStore = SettingsPreferenceDataStore()
            setPreferencesFromResource(R.xml.settings, rootKey)

            findPreference<ListPreference>("language")?.let {
                val userLocale = getLocale()
                val entries = buildList {
                    for (lang in LangList.LOCALES) {
                        if (lang == "SYSTEM") add(getString(R.string.follow_system))
                        else {
                            val locale = Locale.forLanguageTag(lang)
                            add(locale.getDisplayName(locale))
                        }
                    }
                }
                it.entries = entries.toTypedArray()
                it.entryValues = LangList.LOCALES
                if (it.value == "SYSTEM") {
                    it.summary = getString(R.string.follow_system)
                } else {
                    val locale = Locale.forLanguageTag(it.value)
                    it.summary = if (!TextUtils.isEmpty(locale.script)) locale.getDisplayScript(userLocale) else locale.getDisplayName(userLocale)
                }
                it.setOnPreferenceChangeListener { _, newValue ->
                    val locale = getLocale()
                    val config = resources.configuration
                    config.setLocale(locale)
                    hmaApp.resources.updateConfiguration(config, resources.displayMetrics)
                    activity?.recreate()
                    true
                }
            }

            findPreference<ListPreference>("darkTheme")?.setOnPreferenceChangeListener { _, newValue ->
                val newMode = (newValue as String).toInt()
                if (PrefManager.darkTheme != newMode) {
                    AppCompatDelegate.setDefaultNightMode(newMode)
                    activity?.recreate()
                }
                true
            }

            findPreference<SwitchPreferenceCompat>("blackDarkTheme")?.setOnPreferenceChangeListener { _, _ ->
                activity?.recreate()
                true
            }

            configureDataIsolation()

            findPreference<SwitchPreferenceCompat>("bypassRiskyPackageWarning")?.setOnPreferenceChangeListener { _, newValue ->
                activity?.recreate()
                true
            }

            findPreference<Preference>("stopSystemService")?.setOnPreferenceClickListener {
                if (ServiceClient.serviceVersion != 0) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.settings_is_clean_env)
                        .setMessage(R.string.settings_is_clean_env_summary)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            ServiceClient.stopService(true)
                            makeToast(R.string.settings_stop_system_service)
                        }
                        .setNegativeButton(R.string.no) { _, _ ->
                            ServiceClient.stopService(false)
                            makeToast(R.string.settings_stop_system_service)
                        }
                        .setNeutralButton(android.R.string.cancel, null)
                        .show()
                } else makeToast(R.string.home_xposed_service_off)
                true
            }

            findPreference<Preference>("forceCleanEnv")?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(R.string.settings_force_clean_env)
                    .setMessage(R.string.settings_is_clean_env_summary)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        val result = SuUtils.execPrivileged("rm -rf /data/misc/Ciallo_*")
                        if (result) makeToast(R.string.settings_force_clean_env_toast_successful)
                        else makeToast(R.string.settings_permission_denied)
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
                true
            }
        }

        override fun onResume() {
            super.onResume()
            configureDataIsolation()
        }
    }
}
