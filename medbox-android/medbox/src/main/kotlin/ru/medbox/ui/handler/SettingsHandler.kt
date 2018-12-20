package ru.medbox.ui.handler

import android.content.Context
import android.content.Intent
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.activity.SettingsAccountActivity
import ru.medbox.ui.activity.SettingsLegalActivity
import ru.medbox.ui.activity.SettingsSupportActivity
import ru.medbox.ui.activity.SpecializationsActivity

class SettingsHandler(val context: Context?) {

    fun onAccount() {
        if (context is BaseActivity) {
            context.startActivity(Intent(context, SettingsAccountActivity::class.java))
        }
    }

    fun onOurDoctors() {
        if (context is BaseActivity) {
            context.startActivity(Intent(context, SpecializationsActivity::class.java))
        }
    }

    fun onNotifications() {

    }

    fun onDevices() {

    }

    fun onSupport() {
        if (context is BaseActivity) {
            context.startActivity(Intent(context, SettingsSupportActivity::class.java))
        }
    }

    fun onLegal() {
        if (context is BaseActivity) {
            context.startActivity(Intent(context, SettingsLegalActivity::class.java))
        }
    }

}