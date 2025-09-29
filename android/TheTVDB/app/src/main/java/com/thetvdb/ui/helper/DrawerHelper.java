package com.thetvdb.ui.helper;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.thetvdb.R;
import com.thetvdb.util.DefaultPreferences;

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
                        new PrimaryDrawerItem().withIdentifier(1).withName("Serials"),
                        new ExpandableDrawerItem().withName("Favorites")
                                .withSubItems(new SecondaryDrawerItem().withIdentifier(2).withName("My Serials"))
                                .withSubItems(new SecondaryDrawerItem().withIdentifier(3).withName("My Episodes")),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(4).withName("Exit")
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> false)
                .withOnDrawerNavigationListener(clickedView -> false)
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
                                .withOnAccountHeaderListener((view, profile, current) -> false)
                                .build()
                )
                .withOnDrawerItemClickListener(onDrawerItemClickListener)
                .build();

        drawer.setSelection(1);
    }

}
