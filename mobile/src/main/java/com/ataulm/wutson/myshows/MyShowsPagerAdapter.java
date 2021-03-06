package com.ataulm.wutson.myshows;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ataulm.rv.SpacesItemDecoration;
import com.ataulm.vpa.ViewPagerAdapter;
import com.ataulm.wutson.DeveloperError;
import com.ataulm.wutson.R;

final class MyShowsPagerAdapter extends ViewPagerAdapter {

    private final Context context;
    private final Resources resources;
    private final LayoutInflater layoutInflater;
    private final TrackedShowsAdapter trackedShowsAdapter;
    private final WatchlistAdapter watchlistAdapter;

    MyShowsPagerAdapter(Context context, Resources resources, LayoutInflater layoutInflater, TrackedShowsAdapter trackedShowsAdapter, WatchlistAdapter watchlistAdapter) {
        this.context = context;
        this.resources = resources;
        this.layoutInflater = layoutInflater;
        this.trackedShowsAdapter = trackedShowsAdapter;
        this.watchlistAdapter = watchlistAdapter;
    }

    @Override
    protected View getView(ViewGroup container, int position) {
        Page page = Page.from(position);
        switch (page) {
            case ALL:
                return getAllTrackedShowsView(container);
            case WATCHLIST:
                return getWatchlistView(container);
            default:
                throw DeveloperError.because("max " + Page.values().length + " page(s). Got request for page at position: " + position);
        }
    }

    private View getAllTrackedShowsView(ViewGroup container) {
        RecyclerView view = (RecyclerView) layoutInflater.inflate(Page.ALL.getLayoutResId(), container, false);

        int spanCount = resources.getInteger(R.integer.my_shows_span_count);
        int spacing = resources.getDimensionPixelSize(R.dimen.my_shows_item_spacing);
        view.setLayoutManager(new GridLayoutManager(context, spanCount));
        view.addItemDecoration(SpacesItemDecoration.newInstance(spacing, spacing, spanCount));
        view.setAdapter(trackedShowsAdapter);
        return view;
    }

    private View getWatchlistView(ViewGroup container) {
        RecyclerView view = (RecyclerView) layoutInflater.inflate(Page.WATCHLIST.getLayoutResId(), container, false);
        view.setLayoutManager(new LinearLayoutManager(context));
        view.addItemDecoration(SpacesItemDecoration.newInstance(4, 4, 1));
        view.setAdapter(watchlistAdapter);
        return view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Page.values()[position].getTitle(resources);
    }

    @Override
    public int getCount() {
        return Page.values().length;
    }

    private enum Page {

        ALL(R.layout.view_my_shows_page_all, R.string.my_shows_page_all),
        WATCHLIST(R.layout.view_my_shows_page_watchlist, R.string.my_shows_page_watchlist);

        @LayoutRes
        private final int layoutResId;

        @StringRes
        private final int titleResId;

        Page(@LayoutRes int layoutResId, @StringRes int titleResId) {
            this.layoutResId = layoutResId;
            this.titleResId = titleResId;
        }

        @LayoutRes
        int getLayoutResId() {
            return layoutResId;
        }

        String getTitle(Resources resources) {
            return resources.getString(titleResId);
        }

        static Page from(int position) {
            return Page.values()[position];
        }

    }

}
