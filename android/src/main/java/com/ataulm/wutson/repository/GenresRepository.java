package com.ataulm.wutson.repository;

import com.ataulm.wutson.model.Genres;
import com.ataulm.wutson.model.TmdbApi;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

class GenresRepository {

    private final TmdbApi api;
    private final BehaviorSubject<Genres> subject;

    private boolean initialised;

    GenresRepository(TmdbApi api) {
        this.api = api;
        this.subject = BehaviorSubject.create();
    }

    Observable<Genres> getGenres() {
        if (!initialised) {
            refreshGenres();
        }
        return subject;
    }

    private void refreshGenres() {
        api.getGenres()
                .doOnNext(markAsInitialised())
                .subscribeOn(Schedulers.io())
                .subscribe(subject);
    }

    private Action1<Genres> markAsInitialised() {
        return new Action1<Genres>() {

            @Override
            public void call(Genres genres) {
                initialised = true;
            }

        };
    }

}