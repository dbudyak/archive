package ru.medbox.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import ru.medbox.R
import ru.medbox.databinding.ActivityLegalBinding
import ru.medbox.ui.BaseActivity


class SettingsLegalActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLegalBinding = DataBindingUtil.setContentView(this, R.layout.activity_legal)
    }

}
