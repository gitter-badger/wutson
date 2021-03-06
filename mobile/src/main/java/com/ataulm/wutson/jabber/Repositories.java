package com.ataulm.wutson.jabber;

import android.content.Context;

import com.ataulm.wutson.repository.ConfigurationRepository;
import com.ataulm.wutson.repository.DataRepository;
import com.ataulm.wutson.repository.GenresRepository;
import com.ataulm.wutson.repository.SeasonsRepository;
import com.ataulm.wutson.repository.ShowRepository;
import com.ataulm.wutson.repository.TrackedShowsRepository;
import com.ataulm.wutson.repository.WutsonDataRepository;
import com.ataulm.wutson.repository.persistence.LocalDataRepository;
import com.ataulm.wutson.repository.persistence.SqliteLocalDataRepository;
import com.ataulm.wutson.shows.discover.DiscoverShowsRepository;
import com.ataulm.wutson.tmdb.TmdbApi;
import com.google.gson.Gson;

final class Repositories {

    private final Context context;
    private final TmdbApi tmdbApi;

    private WutsonDataRepository dataRepository;
    private DiscoverShowsRepository discoverShows;
    private ConfigurationRepository configuration;
    private LocalDataRepository localData;
    private Gson gson;

    static Repositories newInstance(Context context, TmdbApi tmdbApi) {
        return new Repositories(context.getApplicationContext(), tmdbApi);
    }

    private Repositories(Context context, TmdbApi tmdbApi) {
        this.context = context;
        this.tmdbApi = tmdbApi;
    }

    public DiscoverShowsRepository discoverShows() {
        if (discoverShows == null) {
            ConfigurationRepository configuration = configuration();
            LocalDataRepository localData = localData();
            Gson gson = gson();
            discoverShows = new DiscoverShowsRepository(tmdbApi, configuration, localData, gson);
        }
        return discoverShows;
    }

    private ConfigurationRepository configuration() {
        if (configuration == null) {
            configuration = new ConfigurationRepository(tmdbApi, localData(), gson());
        }
        return configuration;
    }

    private LocalDataRepository localData() {
        if (localData == null) {
            localData = new SqliteLocalDataRepository(context.getContentResolver());
        }
        return localData;
    }

    private Gson gson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public DataRepository dataRepository() {
        if (dataRepository == null) {
            Gson gson = gson();
            LocalDataRepository persistentDataRepo = localData();
            ConfigurationRepository configurationRepo = configuration();

            TrackedShowsRepository trackedShowsRepo = new TrackedShowsRepository(persistentDataRepo, configurationRepo, gson);
            GenresRepository genresRepo = new GenresRepository(tmdbApi, persistentDataRepo, gson);
            ShowRepository showRepo = new ShowRepository(tmdbApi, persistentDataRepo, configurationRepo, gson);
            SeasonsRepository seasonsRepo = new SeasonsRepository(tmdbApi, persistentDataRepo, configurationRepo, showRepo, gson);

            dataRepository = new WutsonDataRepository(trackedShowsRepo, showRepo, seasonsRepo);
        }
        return dataRepository;
    }

}
