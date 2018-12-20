package ru.medbox.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import ru.medbox.R
import ru.medbox.databinding.ActivityAccountBinding
import ru.medbox.ui.BaseActivity


class SettingsAccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityAccountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account)
    }

}
