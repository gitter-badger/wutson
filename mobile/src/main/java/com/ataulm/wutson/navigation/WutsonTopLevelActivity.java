package com.ataulm.wutson.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.ataulm.wutson.R;
import com.ataulm.wutson.browseshows.BrowseShowsActivity;
import com.ataulm.wutson.settings.SettingsActivity;

public abstract class WutsonTopLevelActivity extends WutsonActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_top_level);
        ViewGroup container = (ViewGroup) findViewById(R.id.top_level_container_activity_layout);
        container.removeAllViews();
        getLayoutInflater().inflate(layoutResID, container);

        populateNavigationDrawer();
    }

    private void populateNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_drawer_open_content_description, R.string.nav_drawer_close_content_description);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        final NavigationDrawerView navigationDrawerView = (NavigationDrawerView) drawerLayout.findViewById(R.id.drawer_list);
        navigationDrawerView.setupWithListener(new NavigationDrawerView.OnNavigationClickListener() {

            @Override
            public void onNavigationClick(TopLevelNavigationItem item) {
                closeDrawer();
                switch (item) {
                    case DISCOVER_SHOWS:
                        startActivity(new Intent(WutsonTopLevelActivity.this, BrowseShowsActivity.class));
                        break;
                    case SETTINGS:
                        startActivity(new Intent(WutsonTopLevelActivity.this, SettingsActivity.class));
                        break;
                    default:
                        onNotImplementedActionFor(item);
                }
            }

            private void onNotImplementedActionFor(TopLevelNavigationItem item) {
                String title = item.getTitle();
                getToaster().display(title);
            }

        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        throw new IllegalArgumentException("Item id not implemented");
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(Gravity.START);
    }

}
