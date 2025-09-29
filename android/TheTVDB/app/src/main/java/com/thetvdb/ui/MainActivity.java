package com.thetvdb.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.Drawer;
import com.thetvdb.R;
import com.thetvdb.ui.fragment.FavEpisodesFragment;
import com.thetvdb.ui.fragment.FavSerialsFragment;
import com.thetvdb.ui.fragment.SerialsFragment;
import com.thetvdb.ui.helper.DrawerHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        injector.inject(this);
        checkAuthorizedUser();

        ButterKnife.bind(this);

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        DrawerHelper.initDrawer(this, toolbar, prefs, onDrawerItemClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAuthorizedUser();
    }

    private void checkAuthorizedUser() {
        if (prefs.getTokenHeader().isEmpty() || prefs.getUserId().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void setMainFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();
    }

    Drawer.OnDrawerItemClickListener onDrawerItemClickListener = (view, position, drawerItem) -> {
        int id = (int) drawerItem.getIdentifier();
        switch (id) {
            case 1:
                setMainFragment(new SerialsFragment());
                setTitle("Last updated serials");
                break;
            case 2:
                setMainFragment(new FavSerialsFragment());
                setTitle("Favorite Serials");
                break;
            case 3:
                setMainFragment(new FavEpisodesFragment());
                setTitle("Favorite Episodes");
                break;
            case 4:
                prefs.clear();
                finish();
                break;
        }
        return false;
    };

}
