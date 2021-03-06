package com.ataulm.wutson.episodes;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.ataulm.wutson.BuildConfig;
import com.ataulm.wutson.jabber.Jabber;
import com.ataulm.wutson.R;
import com.ataulm.wutson.shows.ShowId;
import com.ataulm.wutson.navigation.WutsonActivity;
import com.ataulm.wutson.rx.LoggingObserver;
import com.ataulm.wutson.seasons.Season;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EpisodesActivity extends WutsonActivity {

    private static final String KEY_RESET_PAGE_POSITION = BuildConfig.APPLICATION_ID + ".KEY_RESET_PAGE_POSITION";
    private static final int URI_PATH_SEGMENT_SHOW_ID_INDEX = 1;
    private static final int URI_PATH_SEGMENT_SEASON_NUMBER_INDEX = 3;
    private static final int URI_PATH_SEGMENT_EPISODE_NUMBER_INDEX = 5;

    private Subscription episodesSubscription;
    private ViewPager pager;
    private EpisodesPagerAdapter adapter;

    private ShowId showId;
    private int seasonNumber;
    private int episodeNumber;

    private boolean shouldResetPagePosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        Uri data = getIntent().getData();
        showId = new ShowId(data.getPathSegments().get(URI_PATH_SEGMENT_SHOW_ID_INDEX));
        seasonNumber = Integer.parseInt(data.getPathSegments().get(URI_PATH_SEGMENT_SEASON_NUMBER_INDEX));
        episodeNumber = Integer.parseInt(data.getPathSegments().get(URI_PATH_SEGMENT_EPISODE_NUMBER_INDEX));

        pager = (ViewPager) findViewById(R.id.episodes_pager);
        pager.setAdapter(adapter = new EpisodesPagerAdapter(getLayoutInflater()));

        shouldResetPagePosition = savedInstanceState == null || savedInstanceState.getBoolean(KEY_RESET_PAGE_POSITION);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Drawable navigationIcon = getToolbar().getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }

        episodesSubscription = Jabber.dataRepository().getSeason(showId, seasonNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_RESET_PAGE_POSITION, shouldResetPagePosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (!episodesSubscription.isUnsubscribed()) {
            episodesSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    private class Observer extends LoggingObserver<Season> {

        private Observer() {
            super(Jabber.log());
        }

        @Override
        public void onNext(Season season) {
            adapter.update(season);
            if (shouldResetPagePosition) {
                shouldResetPagePosition = false;
                pager.setCurrentItem(adapter.positionOfEpisodeNumber(episodeNumber));
            }
            if (season.size() > 0) {
                setTitle(season.get(0).getShowName());
                getSupportActionBar().setDisplayShowTitleEnabled(true);
            }
        }

    }

}
