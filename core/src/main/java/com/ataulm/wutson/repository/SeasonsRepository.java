package com.ataulm.wutson.repository;

import com.ataulm.wutson.episodes.Episode;
import com.ataulm.wutson.seasons.Season;
import com.ataulm.wutson.seasons.Seasons;
import com.ataulm.wutson.shows.Show;
import com.ataulm.wutson.shows.ShowId;
import com.ataulm.wutson.shows.SimpleDate;
import com.ataulm.wutson.episodes.EpisodeNumber;
import com.ataulm.wutson.repository.persistence.LocalDataRepository;
import com.ataulm.wutson.rx.Function;
import com.ataulm.wutson.tmdb.Configuration;
import com.ataulm.wutson.tmdb.TmdbApi;
import com.ataulm.wutson.tmdb.gson.GsonSeason;
import com.google.gson.Gson;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

import static com.ataulm.wutson.rx.Function.ignoreEmptyStrings;
import static com.ataulm.wutson.rx.Function.jsonTo;

public class SeasonsRepository {

    private final TmdbApi api;
    private final LocalDataRepository localDataRepository;
    private final ConfigurationRepository configurationRepository;
    private final ShowRepository showRepository;
    private final Gson gson;

    public SeasonsRepository(TmdbApi api, LocalDataRepository localDataRepository, ConfigurationRepository configurationRepository, ShowRepository showRepository, Gson gson) {
        this.api = api;
        this.localDataRepository = localDataRepository;
        this.configurationRepository = configurationRepository;
        this.showRepository = showRepository;
        this.gson = gson;
    }

    public Observable<Seasons> getSeasons(final ShowId showId) {
        Observable<Show> showObservable = showRepository.getShowDetails(showId);

        Observable<Season> seasonObservable = showObservable
                .flatMap(extractSeasonSummaries())
                .flatMap(fetchCompleteSeasonFrom(localDataRepository, api, configurationRepository, gson));

        return Observable.combineLatest(showObservable, seasonObservable.toSortedList(), asSeasons());
    }

    private static Func1<Show, Observable<Show.SeasonSummary>> extractSeasonSummaries() {
        return new Func1<Show, Observable<Show.SeasonSummary>>() {

            @Override
            public Observable<Show.SeasonSummary> call(Show show) {
                Iterable<Show.SeasonSummary> seasons = show.getSeasonSummaries();
                return Observable.from(seasons);
            }

        };
    }

    private static Observable<String> fetchJsonSeasonFrom(final LocalDataRepository repository, final ShowId showId, final int seasonNumber) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(repository.readJsonSeason(showId, seasonNumber));
                subscriber.onCompleted();
            }

        });
    }

    private static Action1<GsonSeason> saveTo(final LocalDataRepository localDataRepository, final Gson gson, final ShowId tmdbShowId, final int seasonNumber) {
        return new Action1<GsonSeason>() {

            @Override
            public void call(GsonSeason gsonSeason) {
                String json = gson.toJson(gsonSeason, GsonSeason.class);
                localDataRepository.writeJsonSeason(tmdbShowId, seasonNumber, json);
            }

        };
    }

    private static Func1<Show.SeasonSummary, Observable<Season>> fetchCompleteSeasonFrom(final LocalDataRepository localDataRepository, final TmdbApi api, final ConfigurationRepository configurationRepository, final Gson gson) {
        return new Func1<Show.SeasonSummary, Observable<Season>>() {
            @Override
            public Observable<Season> call(Show.SeasonSummary seasonSummary) {
                ShowId showId = seasonSummary.getShowId();
                int seasonNumber = seasonSummary.getSeasonNumber();

                Observable<GsonSeason> season = fetchJsonSeasonFrom(localDataRepository, showId, seasonNumber)
                        .filter(ignoreEmptyStrings())
                        .map(jsonTo(GsonSeason.class, gson))
                        .switchIfEmpty(api.getSeason(showId.toString(), seasonNumber).doOnNext(saveTo(localDataRepository, gson, showId, seasonNumber)));

                return Observable.zip(
                        configurationRepository.getConfiguration().first(),
                        season,
                        Observable.just(seasonSummary),
                        asSeason());
            }
        };
    }

    private static Func3<Configuration, GsonSeason, Show.SeasonSummary, Season> asSeason() {
        return new Func3<Configuration, GsonSeason, Show.SeasonSummary, Season>() {
            @Override
            public Season call(Configuration configuration, GsonSeason gsonSeason, Show.SeasonSummary seasonSummary) {
                List<Episode> episodes = new ArrayList<>(gsonSeason.episodes.size());
                for (GsonSeason.Episodes.Episode gsonEpisode : gsonSeason.episodes) {
                    episodes.add(new Episode(
                            SimpleDate.from(gsonEpisode.airDate),
                            new EpisodeNumber(gsonSeason.seasonNumber, gsonEpisode.episodeNumber),
                            gsonEpisode.name,
                            gsonEpisode.overview,
                            gsonEpisode.stillPath == null ? URI.create("") :configuration.completeStill(gsonEpisode.stillPath),
                            seasonSummary.getShowName()));
                }

                return new Season(
                        SimpleDate.from(gsonSeason.airDate),
                        gsonSeason.seasonNumber,
                        gsonSeason.overview,
                        configuration.completePoster(gsonSeason.posterPath),
                        episodes);
            }
        };
    }

    private static Func2<Show, List<Season>, Seasons> asSeasons() {
        return new Func2<Show, List<Season>, Seasons>() {

            @Override
            public Seasons call(Show show, List<Season> seasons) {
                return new Seasons(show.getId(), show.getName(), seasons);
            }

        };
    }

    public Observable<Season> getSeason(ShowId showId, final int seasonNumber) {
        return getSeasons(showId)
                .flatMap(Function.<Season>emitEachElement())
                .filter(seasonsNumber(seasonNumber))
                .first();
    }

    private static Func1<Season, Boolean> seasonsNumber(final int seasonId) {
        return new Func1<Season, Boolean>() {

            @Override
            public Boolean call(Season season) {
                return season.getSeasonNumber() == seasonId;
            }

        };
    }

}
