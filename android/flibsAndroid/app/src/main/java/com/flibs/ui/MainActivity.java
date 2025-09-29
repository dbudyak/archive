package com.flibs.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.flibs.R;
import com.flibs.ui.fragment.BooksFragment;
import com.flibs.ui.fragment.CachedBooksFragment;
import com.flibs.ui.helper.DrawerHelper;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        injector.inject(this);

        ButterKnife.bind(this);

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        DrawerHelper.initDrawer(this, toolbar, prefs, onDrawerItemClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setMainFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit();
    }

    Drawer.OnDrawerItemClickListener onDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            int id = (int) drawerItem.getIdentifier();
            switch (id) {
                case 1:
                    setMainFragment(new BooksFragment());
                    setTitle(getString(R.string.drawer_title_book));
                    break;
                case 2:
                    setMainFragment(new CachedBooksFragment());
                    setTitle(getString(R.string.drawer_title_fav));
                    break;
                case 4:
                    prefs.clear();
                    finish();
                    break;
            }
            return false;
        }
    };

}
