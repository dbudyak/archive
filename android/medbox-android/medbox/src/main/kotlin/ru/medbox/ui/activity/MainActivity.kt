package ru.medbox.ui.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import net.hockeyapp.android.CrashManager
import net.hockeyapp.android.UpdateManager
import ru.medbox.BuildConfig
import ru.medbox.R
import ru.medbox.databinding.ActivityMainBinding
import ru.medbox.ui.BaseActivity
import ru.medbox.ui.fragment.CalendarFragment
import ru.medbox.ui.fragment.MedcardFragment
import ru.medbox.ui.fragment.SalutaryFragment
import ru.medbox.ui.fragment.SettingsFragment


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setNavigationBar()

        Fabric.with(this, Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build())
        UpdateManager.register(this)
    }

    private fun setNavigationBar() {
        binding.navigation.setOnNavigationItemSelectedListener {
            val selectedFragment: Fragment
            when (it.itemId) {
                R.id.action_2_calendar -> selectedFragment = CalendarFragment.newInstance()
                R.id.action_3_salutary -> selectedFragment = SalutaryFragment.newInstance()
                R.id.action_4_medcard -> selectedFragment = MedcardFragment.newInstance()
                R.id.action_5_more -> selectedFragment = SettingsFragment.newInstance()
                else -> selectedFragment = ru.medbox.ui.fragment.MainFragment.newInstance()
            }
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame, selectedFragment)
            transaction.commit()
            true
        }
    }

    public override fun onResume() {
        super.onResume()
        CrashManager.register(this)
        binding.navigation.menu.getItem(0).isChecked = true

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frame, ru.medbox.ui.fragment.MainFragment.newInstance())
        transaction.commit()
    }

    public override fun onPause() {
        super.onPause()
        UpdateManager.unregister()
    }

    public override fun onDestroy() {
        super.onDestroy()
        UpdateManager.unregister()
    }

}
