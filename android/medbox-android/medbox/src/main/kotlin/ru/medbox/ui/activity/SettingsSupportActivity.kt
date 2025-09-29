package ru.medbox.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import ru.medbox.R
import ru.medbox.databinding.ActivitySupportBinding
import ru.medbox.ui.BaseActivity


class SettingsSupportActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivitySupportBinding = DataBindingUtil.setContentView(this, R.layout.activity_support)
    }

}
