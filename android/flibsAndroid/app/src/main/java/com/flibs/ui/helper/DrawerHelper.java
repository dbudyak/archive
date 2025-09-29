package com.flibs.ui.helper;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.flibs.R;
import com.flibs.util.DefaultPreferences;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by dbudyak on 30.06.16.
 */
public class DrawerHelper {

    public static void initDrawer(Activity activity, Toolbar toolbar, DefaultPreferences preferences, Drawer.OnDrawerItemClickListener onDrawerItemClickListener) {
        Drawer drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("Поиск книг"),
                        new PrimaryDrawerItem().withIdentifier(2).withName("Полка"),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(4).withName("Выход")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return false;
                    }
                })
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {
                        return false;
                    }
                })
                .withAccountHeader(
                        new AccountHeaderBuilder()
                                .withActivity(activity)
                                .withCompactStyle(false)
                                .withHeaderBackground(R.color.primary)
                                .addProfiles(
                                        new ProfileDrawerItem()
                                                .withIcon(Uri.parse(preferences.getUserPhoto()))
                                                .withName(preferences.getUserName())
                                                .withEmail(preferences.getUserEmail())

                                )
                                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                                    @Override
                                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                                        return false;
                                    }
                                })
                                .build()
                )
                .withOnDrawerItemClickListener(onDrawerItemClickListener)
                .build();

        drawer.setSelection(1);
    }

}
