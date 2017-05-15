package com.thetvdb.ui;

import android.os.Bundle;

import com.thetvdb.R;
import com.thetvdb.ui.fragment.SerialInfoFragment;

import butterknife.ButterKnife;

/**
 * Created by dbudyak on 28.06.16.
 */
public class FavSerialInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cached_serial_info);
        ButterKnife.bind(this);
        injector.inject(this);

        Bundle extras = getIntent().getExtras();
        SerialInfoFragment fragment = new SerialInfoFragment();
        fragment.setArguments(extras);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.cached_serial_container, fragment)
                .commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
